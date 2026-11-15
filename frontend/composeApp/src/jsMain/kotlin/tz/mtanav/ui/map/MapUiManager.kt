@file:NoLiveLiterals

package tz.mtanav.ui.map

import androidx.compose.runtime.NoLiveLiterals
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import tz.mtanav.model.MapBusiness
import tz.mtanav.model.SearchSuggestionDto
import tz.mtanav.model.UserDto

class MapUiManager(
    private val onSearchSubmitted: (String) -> Unit,
    private val onCategorySelected: (String) -> Unit,
    private val onFilterApplied: (minPrice: Double?, maxPrice: Double?, distanceKm: Double?) -> Unit,
    private val onBusinessSelected: (MapBusiness) -> Unit,
    private val onViewShopClicked: (Long) -> Unit,
    private val onNavigateClicked: (MapBusiness) -> Unit,
    private val onMessageShopOwner: (Long) -> Unit,
    private val onNavJobs: () -> Unit,
    private val onNavPromotions: () -> Unit,
    private val onNavEarn: () -> Unit,
    private val onNavAuth: () -> Unit
) {

    private var currentUser: UserDto? = null
    private var isDrawerOpen = false
    private var selectedBusinessId: Long? = null

    init {
        setupEventListeners()
    }

    private fun setupEventListeners() {
        val hamburger = document.getElementById("mtanav-hamburger")
        val backdrop = document.getElementById("mtanav-backdrop")
        val sidebarClose = document.getElementById("mtanav-sidebar-close")

        hamburger?.addEventListener("click", { openDrawer() })
        backdrop?.addEventListener("click", { closeDrawer() })
        sidebarClose?.addEventListener("click", { closeDrawer() })

        // ESC Key listener to close drawer
        window.addEventListener("keydown", { event ->
            if (event is KeyboardEvent && event.key == "Escape") {
                if (isDrawerOpen) closeDrawer()
            }
        })

        // Search Input listener
        val searchInput = document.getElementById("mtanav-search-input") as? HTMLInputElement
        val searchClear = document.getElementById("mtanav-search-clear") as? HTMLButtonElement

        searchInput?.addEventListener("input", {
            val q = searchInput.value
            if (q.isNotEmpty()) {
                searchClear?.asDynamic()?.style?.display = "block"
            } else {
                searchClear?.asDynamic()?.style?.display = "none"
            }
        })

        searchInput?.addEventListener("keydown", { event ->
            if (event is KeyboardEvent && event.key == "Enter") {
                val q = searchInput.value.trim()
                if (q.isNotEmpty()) {
                    closeDrawer()
                    onSearchSubmitted(q)
                }
            }
        })

        searchClear?.addEventListener("click", {
            if (searchInput != null) {
                searchInput.value = ""
                searchClear.asDynamic().style.display = "none"
                onSearchSubmitted("")
            }
        })

        // Navigation Links
        document.getElementById("nav-discover")?.addEventListener("click", {
            closeDrawer()
            setActiveNavLink("nav-discover")
        })

        document.getElementById("nav-jobs")?.addEventListener("click", {
            closeDrawer()
            onNavJobs()
        })

        document.getElementById("nav-promotions")?.addEventListener("click", {
            closeDrawer()
            onNavPromotions()
        })

        document.getElementById("nav-earn")?.addEventListener("click", {
            closeDrawer()
            onNavEarn()
        })

        document.getElementById("nav-auth")?.addEventListener("click", {
            closeDrawer()
            onNavAuth()
        })

        // Category Chips
        val chipContainer = document.getElementById("mtanav-category-chips")
        val chips = chipContainer?.getElementsByClassName("mtanav-chip")
        if (chips != null) {
            for (i in 0 until chips.length) {
                val chip = chips.item(i) as? HTMLElement ?: continue
                chip.addEventListener("click", {
                    for (j in 0 until chips.length) {
                        (chips.item(j) as? HTMLElement)?.classList?.remove("active")
                    }
                    chip.classList.add("active")
                    val cat = chip.getAttribute("data-cat") ?: "ALL"
                    closeDrawer()
                    onCategorySelected(cat)
                })
            }
        }

        // Advanced Filter Toggle
        val filterToggleBtn = document.getElementById("mtanav-filter-toggle-btn")
        val filterPanel = document.getElementById("mtanav-filter-panel")
        filterToggleBtn?.addEventListener("click", {
            val curDisplay = filterPanel?.asDynamic()?.style?.display
            if (curDisplay == "none" || curDisplay == "" || curDisplay == null) {
                filterPanel?.asDynamic()?.style?.display = "flex"
            } else {
                filterPanel?.asDynamic()?.style?.display = "none"
            }
        })

        val distanceInput = document.getElementById("filter-distance") as? HTMLInputElement
        val distanceVal = document.getElementById("filter-distance-val")
        distanceInput?.addEventListener("input", {
            distanceVal?.textContent = "${distanceInput.value} km"
        })

        document.getElementById("btn-apply-filters")?.addEventListener("click", {
            val minP = (document.getElementById("filter-min-price") as? HTMLInputElement)?.value?.toDoubleOrNull()
            val maxP = (document.getElementById("filter-max-price") as? HTMLInputElement)?.value?.toDoubleOrNull()
            val dist = distanceInput?.value?.toDoubleOrNull()
            closeDrawer()
            onFilterApplied(minP, maxP, dist)
        })

        document.getElementById("btn-reset-filters")?.addEventListener("click", {
            (document.getElementById("filter-min-price") as? HTMLInputElement)?.value = ""
            (document.getElementById("filter-max-price") as? HTMLInputElement)?.value = ""
            if (distanceInput != null) {
                distanceInput.value = "25"
                distanceVal?.textContent = "25 km"
            }
            closeDrawer()
            onFilterApplied(null, null, null)
        })
    }

    fun setUser(user: UserDto?) {
        this.currentUser = user
        val navAuthText = document.getElementById("nav-auth-text")
        if (navAuthText != null) {
            navAuthText.textContent = if (user != null) "👤 ${user.fullName}" else "Login / Register"
        }
    }

    fun openDrawer() {
        val sidebar = document.getElementById("mtanav-sidebar")
        val backdrop = document.getElementById("mtanav-backdrop")
        sidebar?.classList?.add("open")
        backdrop?.classList?.add("open")
        isDrawerOpen = true
    }

    fun closeDrawer() {
        val sidebar = document.getElementById("mtanav-sidebar")
        val backdrop = document.getElementById("mtanav-backdrop")
        sidebar?.classList?.remove("open")
        backdrop?.classList?.remove("open")
        isDrawerOpen = false
    }

    private fun setActiveNavLink(activeId: String) {
        val navIds = listOf("nav-discover", "nav-jobs", "nav-promotions", "nav-earn", "nav-auth")
        navIds.forEach { id ->
            val el = document.getElementById(id)
            if (id == activeId) el?.classList?.add("active") else el?.classList?.remove("active")
        }
    }

    fun updateResultCount(count: Int) {
        val countEl = document.getElementById("mtanav-result-count")
        if (countEl != null) {
            countEl.textContent = "$count Location${if (count != 1) "s" else ""} Found"
        }
    }

    fun renderBusinessList(businesses: List<MapBusiness>, selected: MapBusiness?) {
        val container = document.getElementById("mtanav-business-list") ?: return
        container.innerHTML = ""
        updateResultCount(businesses.size)

        if (businesses.isEmpty()) {
            container.innerHTML = """
                <div style="padding:24px; text-align:center; color:var(--secondary-text);">
                    <div style="font-size:28px;">🔍</div>
                    <div style="font-weight:700; margin-top:8px;">No locations found</div>
                    <div style="font-size:12px; margin-top:4px;">Try adjusting your category filter or search keywords.</div>
                </div>
            """.trimIndent()
            return
        }

        businesses.forEach { b ->
            val isSelected = (selected?.id == b.id)
            val card = document.createElement("div") as HTMLDivElement
            card.className = "mtanav-card ${if (isSelected) "selected" else ""}"

            val matchBadge = if (b.matchingProduct != null) {
                "<span style='font-size:11px; background:var(--light); color:var(--primary); padding:2px 6px; border-radius:4px; font-weight:700;'>📦 ${b.matchingProduct}${if (b.price != null) " • TZS ${b.price.toInt()}" else ""}</span>"
            } else if (b.matchingService != null) {
                "<span style='font-size:11px; background:var(--light); color:var(--primary); padding:2px 6px; border-radius:4px; font-weight:700;'>⚙️ ${b.matchingService}</span>"
            } else ""

            val displayName = b.name.replace("Demo", "Vunjabei")

            card.innerHTML = """
                <div style="display:flex; justify-content:space-between; align-items:flex-start;">
                    <div style="font-weight:700; font-size:14px; color:var(--text);">${displayName}</div>
                    <span style="font-size:12px; font-weight:700; color:#D97706;">⭐ ${b.rating}</span>
                </div>
                <div style="font-size:12px; color:var(--secondary-text);">${b.address} • ${b.city}</div>
                $matchBadge
            """.trimIndent()

            card.addEventListener("click", {
                selectedBusinessId = b.id
                onBusinessSelected(b)
            })

            container.appendChild(card)
        }
    }

    fun showSuggestions(suggestions: List<SearchSuggestionDto>) {
        val dropdown = document.getElementById("mtanav-suggestions-dropdown") ?: return
        dropdown.innerHTML = ""

        if (suggestions.isEmpty()) {
            dropdown.asDynamic().style.display = "none"
            return
        }

        dropdown.asDynamic().style.display = "block"
        suggestions.forEach { sug ->
            val item = document.createElement("div") as HTMLDivElement
            item.asDynamic().style.padding = "8px 12px"
            item.asDynamic().style.cursor = "pointer"
            item.asDynamic().style.fontSize = "13px"
            item.asDynamic().style.borderBottom = "1px solid var(--border)"

            val subTitle = sug.businessName ?: sug.availabilityStatus ?: ""
            item.innerHTML = """
                <div style="font-weight:600; color:var(--text);">${sug.name}</div>
                <div style="font-size:11px; color:var(--secondary-text);">${sug.type} • $subTitle</div>
            """.trimIndent()

            item.addEventListener("click", {
                dropdown.asDynamic().style.display = "none"
                val searchInput = document.getElementById("mtanav-search-input") as? HTMLInputElement
                if (searchInput != null) searchInput.value = sug.name
                closeDrawer()
                onSearchSubmitted(sug.name)
            })

            dropdown.appendChild(item)
        }
    }
}
