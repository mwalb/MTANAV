package tz.mtanav.map

import kotlinx.browser.document
import kotlinx.browser.window
import tz.mtanav.config.ApiConfig
import tz.mtanav.model.MapBusiness
import tz.mtanav.model.hasValidCoordinates

@Suppress("UNUSED_VARIABLE")
class WebNativeMapController(
    private val containerId: String = "map",
    private val onBusinessSelected: (MapBusiness) -> Unit,
    private val onViewShopClicked: (Long) -> Unit,
    private val onNavigateClicked: (MapBusiness) -> Unit,
    private val onHamburgerClicked: () -> Unit = {},
    private val onSearchClicked: (String) -> Unit = {},
    private val onProductsClicked: () -> Unit = {},
    private val onServicesClicked: () -> Unit = {},
    private val onPromotionsClicked: () -> Unit = {},
    private val onJobsClicked: () -> Unit = {},
    private val onEarnClicked: () -> Unit = {},
    private val onAuthClicked: () -> Unit = {}
) : NativeMapController {

    private var jsMap: dynamic = null
    private val markersMap = mutableMapOf<Long, dynamic>()
    private var activeInfoWindow: dynamic = null

    fun updateNativeMapMarkersFromJs(rawList: Array<dynamic>) {
        val list = rawList.map { item ->
            MapBusiness(
                id = (item.id as Number).toLong(),
                name = item.name.toString(),
                latitude = (item.latitude as Number).toDouble(),
                longitude = (item.longitude as Number).toDouble(),
                rating = (item.rating as? Number)?.toDouble() ?: 0.0,
                reviewCount = (item.reviewCount as? Number)?.toInt() ?: 0,
                address = item.address?.toString() ?: "",
                city = item.city?.toString() ?: "Dar es Salaam",
                buildingName = item.buildingName?.toString(),
                shopNumber = item.shopNumber?.toString(),
                matchingProduct = item.matchingItemName?.toString(),
                matchingService = null,
                price = (item.startingPrice as? Number)?.toDouble(),
                imageUrl = item.logoUrl?.toString()
            )
        }
        showBusinesses(list)
    }

    fun focusBusinessById(id: Long) {
        val target = markersMap[id]
        if (target != null && jsMap != null) {
            js("google.maps.event.trigger(target, 'click')")
        }
    }

    override fun initialize() {
        ApiConfig.log("MAP_WEB", "MAP INIT START")
        val container = document.getElementById(containerId) ?: return

        val lat = ApiConfig.DEFAULT_LATITUDE
        val lng = ApiConfig.DEFAULT_LONGITUDE
        val zoomVal = ApiConfig.DEFAULT_ZOOM

        jsMap = js("""
            new google.maps.Map(container, {
                zoom: zoomVal,
                center: { lat: lat, lng: lng },
                disableDefaultUI: true,
                zoomControl: false,
                mapTypeControl: false,
                scaleControl: false,
                streetViewControl: false,
                rotateControl: false,
                fullscreenControl: false
            })
        """)

        val mapInst = jsMap
        val self = this
        val onAuth = onAuthClicked
        val onPromotions = onPromotionsClicked
        val onJobs = onJobsClicked
        val onEarn = onEarnClicked
        val onHamb = onHamburgerClicked

        js("""
            (function() {
                var API_BASE = "https://mtanav.fly.dev/api";

                window.mtanavUpdateMapMarkers = function(rawList) {
                    if (rawList && Array.isArray(rawList)) {
                        self.updateNativeMapMarkersFromJs(rawList);
                    }
                };

                window.mtanavFocusBusiness = function(businessId) {
                    if (businessId) {
                        self.focusBusinessById(businessId);
                    }
                };

                var sidebar = document.getElementById('native-sidebar-panel');
                var backdrop = document.getElementById('native-sidebar-backdrop');
                var closeBtn = document.getElementById('sidebar-close-btn');
                var authBtn = document.getElementById('sidebar-auth-btn');
                var searchInput = document.getElementById('sidebar-search-input');
                var suggestionsDropdown = document.getElementById('search-suggestions-dropdown');

                var viewCategories = document.getElementById('view-categories');
                var viewProducts = document.getElementById('view-products');
                var viewServices = document.getElementById('view-services');
                var viewFilterResults = document.getElementById('view-filter-results');

                var productsContainer = document.getElementById('products-list-container');
                var servicesContainer = document.getElementById('services-list-container');
                var filterResultsContainer = document.getElementById('filter-results-container');

                var navProducts = document.getElementById('nav-products');
                var navServices = document.getElementById('nav-services');
                var navPromotions = document.getElementById('nav-promotions');
                var navJobs = document.getElementById('nav-jobs');
                var navEarn = document.getElementById('nav-earn');

                var productsBackBtn = document.getElementById('products-back-btn');
                var servicesBackBtn = document.getElementById('services-back-btn');
                var filterBackBtn = document.getElementById('filter-back-btn');

                // Filter Form Controls
                var activeSelectedItem = null;
                var userGeoLat = null;
                var userGeoLng = null;

                var btnUseMyLocation = document.getElementById('btn-use-my-location');
                var geoLocationStatus = document.getElementById('geo-location-status');
                var btnGeoLabel = document.getElementById('geo-btn-label');

                var btnApplyFilters = document.getElementById('btn-apply-filters');
                var btnClearFilters = document.getElementById('btn-clear-filters');

                var filterRegion = document.getElementById('filter-region');
                var filterDistrict = document.getElementById('filter-district');
                var filterArea = document.getElementById('filter-area');
                var filterDistance = document.getElementById('filter-distance');
                var filterMinPrice = document.getElementById('filter-min-price');
                var filterMaxPrice = document.getElementById('filter-max-price');
                var filterSort = document.getElementById('filter-sort');
                var filterMinRating = document.getElementById('filter-min-rating');
                var filterInStock = document.getElementById('filter-in-stock');
                var filterVerifiedOnly = document.getElementById('filter-verified-only');
                var filterHasPromotions = document.getElementById('filter-has-promotions');
                var filterFreeOnly = document.getElementById('filter-free-only');

                var containerInStock = document.getElementById('container-in-stock');
                var containerFreeServices = document.getElementById('container-free-services');

                var filterItemName = document.getElementById('filter-item-name');
                var filterItemBadge = document.getElementById('filter-item-badge');

                var cachedProducts = null;
                var cachedServices = null;
                var currentView = 'categories';
                var previousView = 'categories';

                function openSidebar() {
                    if (sidebar) sidebar.classList.add('open');
                    if (backdrop) backdrop.classList.add('active');
                }

                function closeSidebar() {
                    if (sidebar) sidebar.classList.remove('open');
                    if (backdrop) backdrop.classList.remove('active');
                    if (suggestionsDropdown) suggestionsDropdown.style.display = 'none';
                }

                function switchView(targetView) {
                    previousView = currentView;
                    currentView = targetView;

                    if (viewCategories) viewCategories.style.display = targetView === 'categories' ? 'flex' : 'none';
                    if (viewProducts) viewProducts.style.display = targetView === 'products' ? 'flex' : 'none';
                    if (viewServices) viewServices.style.display = targetView === 'services' ? 'flex' : 'none';
                    if (viewFilterResults) viewFilterResults.style.display = targetView === 'filter_results' ? 'flex' : 'none';

                    if (suggestionsDropdown) suggestionsDropdown.style.display = 'none';
                }

                // Quick price chip shortcuts
                var priceChips = document.querySelectorAll('.price-chip');
                if (priceChips) {
                    Array.prototype.forEach.call(priceChips, function(chip) {
                        chip.onclick = function(e) {
                            e.preventDefault();
                            var min = chip.getAttribute('data-min') || '';
                            var max = chip.getAttribute('data-max') || '';
                            if (filterMinPrice) filterMinPrice.value = min;
                            if (filterMaxPrice) filterMaxPrice.value = max;
                            Array.prototype.forEach.call(priceChips, function(c) { c.classList.remove('active'); });
                            chip.classList.add('active');
                        };
                    });
                }

                // Geolocation button handler
                if (btnUseMyLocation) {
                    btnUseMyLocation.onclick = function(e) {
                        e.preventDefault();
                        if (!navigator.geolocation) {
                            if (geoLocationStatus) {
                                geoLocationStatus.style.display = 'block';
                                geoLocationStatus.innerText = 'Geolocation is not supported by your browser.';
                            }
                            return;
                        }
                        if (btnGeoLabel) btnGeoLabel.innerText = 'Locating...';
                        navigator.geolocation.getCurrentPosition(
                            function(position) {
                                userGeoLat = position.coords.latitude;
                                userGeoLng = position.coords.longitude;
                                if (btnGeoLabel) btnGeoLabel.innerText = '✓ Location Acquired';
                                if (geoLocationStatus) {
                                    geoLocationStatus.style.display = 'block';
                                    geoLocationStatus.innerText = '📍 Lat: ' + userGeoLat.toFixed(4) + ', Lng: ' + userGeoLng.toFixed(4);
                                }
                            },
                            function(err) {
                                userGeoLat = null;
                                userGeoLng = null;
                                if (btnGeoLabel) btnGeoLabel.innerText = 'Use My Current Location';
                                if (geoLocationStatus) {
                                    geoLocationStatus.style.display = 'block';
                                    geoLocationStatus.innerText = 'Location permission denied. Please select location manually.';
                                }
                            },
                            { timeout: 10000, enableHighAccuracy: true }
                        );
                    };
                }

                function openFilterInterface(item) {
                    activeSelectedItem = item;
                    if (filterItemName) filterItemName.innerText = item.name;
                    if (filterItemBadge) filterItemBadge.innerText = (item.type || 'PRODUCT').toUpperCase() + ' DISCOVERY';

                    if (item.type === 'service') {
                        if (containerInStock) containerInStock.style.display = 'none';
                        if (containerFreeServices) containerFreeServices.style.display = 'flex';
                    } else {
                        if (containerInStock) containerInStock.style.display = 'flex';
                        if (containerFreeServices) containerFreeServices.style.display = 'none';
                    }

                    switchView('filter_results');
                    applyCurrentFilters();
                }

                function applyCurrentFilters() {
                    if (!activeSelectedItem) return;

                    var params = [];
                    if (activeSelectedItem.type === 'product' && activeSelectedItem.id) {
                        params.push("productId=" + activeSelectedItem.id);
                    } else if (activeSelectedItem.type === 'service' && activeSelectedItem.id) {
                        params.push("serviceId=" + activeSelectedItem.id);
                    } else if (activeSelectedItem.type === 'search' || activeSelectedItem.name) {
                        params.push("query=" + encodeURIComponent(activeSelectedItem.name));
                    }

                    if (filterRegion && filterRegion.value) params.push("region=" + encodeURIComponent(filterRegion.value));
                    if (filterDistrict && filterDistrict.value) params.push("district=" + encodeURIComponent(filterDistrict.value));
                    if (filterArea && filterArea.value.trim()) params.push("area=" + encodeURIComponent(filterArea.value.trim()));

                    if (filterDistance && filterDistance.value && userGeoLat && userGeoLng) {
                        params.push("maxDistanceKm=" + filterDistance.value);
                        params.push("userLat=" + userGeoLat);
                        params.push("userLng=" + userGeoLng);
                    }

                    if (filterMinPrice && filterMinPrice.value) params.push("minPrice=" + filterMinPrice.value);
                    if (filterMaxPrice && filterMaxPrice.value) params.push("maxPrice=" + filterMaxPrice.value);
                    if (filterSort && filterSort.value) params.push("sort=" + filterSort.value);
                    if (filterMinRating && filterMinRating.value) params.push("minRating=" + filterMinRating.value);

                    if (filterVerifiedOnly && filterVerifiedOnly.checked) params.push("verifiedOnly=true");
                    if (filterInStock && filterInStock.checked && activeSelectedItem.type !== 'service') params.push("inStockOnly=true");
                    if (filterHasPromotions && filterHasPromotions.checked) params.push("hasPromotions=true");
                    if (filterFreeOnly && filterFreeOnly.checked && activeSelectedItem.type === 'service') params.push("freeOnly=true");

                    var queryString = params.join("&");
                    fetchAndShowMapBusinesses(queryString, activeSelectedItem.name, "Filtered Businesses");
                }

                function clearAllFilters() {
                    if (filterRegion) filterRegion.value = "Dar es Salaam";
                    if (filterDistrict) filterDistrict.value = "";
                    if (filterArea) filterArea.value = "";
                    if (filterDistance) filterDistance.value = "";
                    if (filterMinPrice) filterMinPrice.value = "";
                    if (filterMaxPrice) filterMaxPrice.value = "";
                    if (filterSort) filterSort.value = "";
                    if (filterMinRating) filterMinRating.value = "";

                    if (filterInStock) filterInStock.checked = true;
                    if (filterVerifiedOnly) filterVerifiedOnly.checked = true;
                    if (filterHasPromotions) filterHasPromotions.checked = false;
                    if (filterFreeOnly) filterFreeOnly.checked = false;

                    userGeoLat = null;
                    userGeoLng = null;
                    if (btnGeoLabel) btnGeoLabel.innerText = "Use My Current Location";
                    if (geoLocationStatus) geoLocationStatus.style.display = "none";

                    if (priceChips) {
                        Array.prototype.forEach.call(priceChips, function(c) { c.classList.remove('active'); });
                    }

                    applyCurrentFilters();
                }

                if (btnApplyFilters) {
                    btnApplyFilters.onclick = function(e) {
                        e.preventDefault();
                        applyCurrentFilters();
                    };
                }

                if (btnClearFilters) {
                    btnClearFilters.onclick = function(e) {
                        e.preventDefault();
                        clearAllFilters();
                    };
                }

                function fetchAndShowMapBusinesses(urlParams, filterTitleText, filterSubText) {
                    if (filterResultsContainer) {
                        filterResultsContainer.innerHTML = '<div class="sidebar-state-box"><div class="sidebar-spinner"></div>Finding matching shops on map...</div>';
                    }

                    var fullUrl = API_BASE + "/businesses/map" + (urlParams ? "?" + urlParams : "");
                    fetch(fullUrl)
                        .then(function(res) { return res.json(); })
                        .then(function(json) {
                            var list = (json && json.data) ? json.data : [];
                            if (window.mtanavUpdateMapMarkers) {
                                window.mtanavUpdateMapMarkers(list);
                            }
                            renderFilterResults(list, filterTitleText, filterSubText);
                        })
                        .catch(function(err) {
                            if (filterResultsContainer) {
                                filterResultsContainer.innerHTML = '<div class="sidebar-state-box">Failed to load matching locations.<br><button id="retry-filter-btn" style="margin-top:8px; background:#E8AD4A; border:none; padding:8px 16px; border-radius:6px; font-weight:bold; cursor:pointer; color:#124E4A;">Retry</button></div>';
                                var retryBtn = document.getElementById('retry-filter-btn');
                                if (retryBtn) {
                                    retryBtn.onclick = function() {
                                        fetchAndShowMapBusinesses(urlParams, filterTitleText, filterSubText);
                                    };
                                }
                            }
                        });
                }

                function renderFilterResults(businesses, titleText, subText) {
                    if (!filterResultsContainer) return;

                    var bannerHtml = '<div class="active-filter-banner"><div><div class="filter-title">' + (titleText || 'Filtered Results') + '</div><div class="filter-sub">' + (subText || (businesses.length + ' Locations Found')) + '</div></div></div>';

                    if (businesses.length === 0) {
                        filterResultsContainer.innerHTML = bannerHtml + '<div class="sidebar-state-box">No shops found matching all selected criteria.<br><small style="color:#E8AD4A;">Try expanding your price range or distance radius.</small></div>';
                        return;
                    }

                    var cardsHtml = businesses.map(function(b) {
                        var priceStr = b.startingPrice ? (' • TSh ' + Math.round(b.startingPrice).toLocaleString('en-US')) : '';
                        var matchMsg = b.matchingItemName ? ('<div style="color:#E8AD4A; font-size:12px; font-weight:bold; margin-top:2px;">Item: ' + b.matchingItemName + priceStr + '</div>') : '';
                        return '<div class="list-item" id="filter-biz-' + b.id + '">' +
                            '<div class="item-text">' +
                                '<div class="item-title">' + b.name + '</div>' +
                                '<div class="item-desc">⭐ ' + b.rating + ' (' + b.reviewCount + ') • ' + b.address + '</div>' +
                                matchMsg +
                            '</div>' +
                            '<div class="menu-arrow"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#E8AD4A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"></polyline></svg></div>' +
                        '</div>';
                    }).join('');

                    filterResultsContainer.innerHTML = bannerHtml + cardsHtml;

                    businesses.forEach(function(b) {
                        var el = document.getElementById('filter-biz-' + b.id);
                        if (el) {
                            el.onclick = function() {
                                if (window.mtanavFocusBusiness) {
                                    window.mtanavFocusBusiness(b.id);
                                }
                                if (window.innerWidth < 1024) {
                                    closeSidebar();
                                }
                            };
                        }
                    });
                }

                function loadProductsList() {
                    switchView('products');
                    if (cachedProducts) {
                        renderProductsList(cachedProducts);
                        return;
                    }

                    if (productsContainer) {
                        productsContainer.innerHTML = '<div class="sidebar-state-box"><div class="sidebar-spinner"></div>Loading products...</div>';
                    }

                    fetch(API_BASE + "/products")
                        .then(function(res) { return res.json(); })
                        .then(function(json) {
                            cachedProducts = (json && json.data) ? json.data : [];
                            renderProductsList(cachedProducts);
                        })
                        .catch(function() {
                            if (productsContainer) {
                                productsContainer.innerHTML = '<div class="sidebar-state-box">Failed to load products.<br><button id="retry-products-btn" style="margin-top:8px; background:#E8AD4A; border:none; padding:8px 16px; border-radius:6px; font-weight:bold; cursor:pointer;">Retry</button></div>';
                                var retry = document.getElementById('retry-products-btn');
                                if (retry) retry.onclick = loadProductsList;
                            }
                        });
                }

                function renderProductsList(products) {
                    if (!productsContainer) return;
                    if (products.length === 0) {
                        productsContainer.innerHTML = '<div class="sidebar-state-box">No products available.</div>';
                        return;
                    }

                    var html = products.map(function(p) {
                        return '<div class="list-item" id="prod-item-' + p.id + '">' +
                            '<div class="item-text">' +
                                '<div class="item-title">' + p.name + '</div>' +
                                '<div class="item-desc">' + (p.description || 'Verified local shop item') + '</div>' +
                            '</div>' +
                            '<div class="menu-arrow"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.6)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"></polyline></svg></div>' +
                        '</div>';
                    }).join('');

                    productsContainer.innerHTML = html;

                    products.forEach(function(p) {
                        var el = document.getElementById('prod-item-' + p.id);
                        if (el) {
                            el.onclick = function(e) {
                                e.stopPropagation();
                                openFilterInterface({ type: 'product', id: p.id, name: p.name });
                            };
                        }
                    });
                }

                function loadServicesList() {
                    switchView('services');
                    if (cachedServices) {
                        renderServicesList(cachedServices);
                        return;
                    }

                    if (servicesContainer) {
                        servicesContainer.innerHTML = '<div class="sidebar-state-box"><div class="sidebar-spinner"></div>Loading services...</div>';
                    }

                    fetch(API_BASE + "/categories/services")
                        .then(function(res) { return res.json(); })
                        .then(function(json) {
                            cachedServices = (json && json.data) ? json.data : [];
                            renderServicesList(cachedServices);
                        })
                        .catch(function() {
                            if (servicesContainer) {
                                servicesContainer.innerHTML = '<div class="sidebar-state-box">Failed to load services.<br><button id="retry-services-btn" style="margin-top:8px; background:#E8AD4A; border:none; padding:8px 16px; border-radius:6px; font-weight:bold; cursor:pointer;">Retry</button></div>';
                                var retry = document.getElementById('retry-services-btn');
                                if (retry) retry.onclick = loadServicesList;
                            }
                        });
                }

                function renderServicesList(services) {
                    if (!servicesContainer) return;
                    if (services.length === 0) {
                        servicesContainer.innerHTML = '<div class="sidebar-state-box">No services available.</div>';
                        return;
                    }

                    var html = services.map(function(s) {
                        return '<div class="list-item" id="srv-item-' + s.id + '">' +
                            '<div class="item-text">' +
                                '<div class="item-title">' + s.name + '</div>' +
                                '<div class="item-desc">' + (s.description || 'Verified local service provider') + '</div>' +
                            '</div>' +
                            '<div class="menu-arrow"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.6)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"></polyline></svg></div>' +
                        '</div>';
                    }).join('');

                    servicesContainer.innerHTML = html;

                    services.forEach(function(s) {
                        var el = document.getElementById('srv-item-' + s.id);
                        if (el) {
                            el.onclick = function(e) {
                                e.stopPropagation();
                                openFilterInterface({ type: 'service', id: s.id, name: s.name });
                            };
                        }
                    });
                }

                if (productsBackBtn) {
                    productsBackBtn.onclick = function(e) {
                        e.stopPropagation();
                        switchView('categories');
                        fetchAndShowMapBusinesses('', 'All Locations', 'Showing all verified shops & services');
                    };
                }

                if (servicesBackBtn) {
                    servicesBackBtn.onclick = function(e) {
                        e.stopPropagation();
                        switchView('categories');
                        fetchAndShowMapBusinesses('', 'All Locations', 'Showing all verified shops & services');
                    };
                }

                if (filterBackBtn) {
                    filterBackBtn.onclick = function(e) {
                        e.stopPropagation();
                        if (previousView === 'products') {
                            switchView('products');
                        } else if (previousView === 'services') {
                            switchView('services');
                        } else {
                            switchView('categories');
                        }
                        fetchAndShowMapBusinesses('', 'All Locations', 'Showing all verified shops & services');
                    };
                }

                if (navProducts) {
                    navProducts.onclick = function(e) {
                        e.stopPropagation();
                        loadProductsList();
                    };
                }

                if (navServices) {
                    navServices.onclick = function(e) {
                        e.stopPropagation();
                        loadServicesList();
                    };
                }

                var searchTimer = null;
                if (searchInput) {
                    searchInput.oninput = function() {
                        var query = (searchInput.value || '').trim();
                        if (searchTimer) clearTimeout(searchTimer);

                        if (query.length < 2) {
                            if (suggestionsDropdown) suggestionsDropdown.style.display = 'none';
                            if (currentView === 'products' && cachedProducts) {
                                var filtered = cachedProducts.filter(function(p) { return p.name.toLowerCase().indexOf(query.toLowerCase()) !== -1; });
                                renderProductsList(filtered);
                            } else if (currentView === 'services' && cachedServices) {
                                var filtered = cachedServices.filter(function(s) { return s.name.toLowerCase().indexOf(query.toLowerCase()) !== -1; });
                                renderServicesList(filtered);
                            }
                            return;
                        }

                        if (currentView === 'products' && cachedProducts) {
                            var filteredP = cachedProducts.filter(function(p) { return p.name.toLowerCase().indexOf(query.toLowerCase()) !== -1; });
                            renderProductsList(filteredP);
                        } else if (currentView === 'services' && cachedServices) {
                            var filteredS = cachedServices.filter(function(s) { return s.name.toLowerCase().indexOf(query.toLowerCase()) !== -1; });
                            renderServicesList(filteredS);
                        }

                        searchTimer = setTimeout(function() {
                            fetch(API_BASE + "/search/suggestions?q=" + encodeURIComponent(query))
                                .then(function(res) { return res.json(); })
                                .then(function(json) {
                                    var suggestions = (json && json.data) ? json.data : [];
                                    renderSuggestions(suggestions);
                                })
                                .catch(function() {
                                    if (suggestionsDropdown) suggestionsDropdown.style.display = 'none';
                                });
                        }, 200);
                    };

                    searchInput.onkeydown = function(e) {
                        if (e.key === 'Enter') {
                            e.stopPropagation();
                            var query = (searchInput.value || '').trim();
                            if (suggestionsDropdown) suggestionsDropdown.style.display = 'none';
                            if (query) {
                                openFilterInterface({ type: 'search', id: null, name: query });
                            }
                        }
                    };
                }

                function renderSuggestions(suggestions) {
                    if (!suggestionsDropdown) return;
                    if (suggestions.length === 0) {
                        suggestionsDropdown.style.display = 'none';
                        return;
                    }

                    var html = suggestions.map(function(item) {
                        var tag = item.category || item.type || 'Search';
                        return '<div class="suggestion-item" data-text="' + item.text + '">' +
                            '<span>' + item.text + '</span>' +
                            '<span class="suggestion-type">' + tag + '</span>' +
                        '</div>';
                    }).join('');

                    suggestionsDropdown.innerHTML = html;
                    suggestionsDropdown.style.display = 'block';

                    Array.prototype.forEach.call(suggestionsDropdown.children, function(child) {
                        child.onclick = function() {
                            var text = child.getAttribute('data-text');
                            if (searchInput) searchInput.value = text;
                            suggestionsDropdown.style.display = 'none';
                            openFilterInterface({ type: 'search', id: null, name: text });
                        };
                    });
                }

                document.addEventListener('click', function(e) {
                    if (suggestionsDropdown && !suggestionsDropdown.contains(e.target) && e.target !== searchInput) {
                        suggestionsDropdown.style.display = 'none';
                    }
                });

                if (closeBtn) {
                    closeBtn.onclick = function(e) {
                        e.stopPropagation();
                        closeSidebar();
                    };
                }

                if (backdrop) {
                    backdrop.onclick = function() {
                        closeSidebar();
                    };
                }

                if (authBtn) {
                    authBtn.onclick = function(e) {
                        e.stopPropagation();
                        closeSidebar();
                        onAuth();
                    };
                }

                if (navPromotions) {
                    navPromotions.onclick = function(e) {
                        e.stopPropagation();
                        closeSidebar();
                        onPromotions();
                    };
                }

                if (navJobs) {
                    navJobs.onclick = function(e) {
                        e.stopPropagation();
                        closeSidebar();
                        onJobs();
                    };
                }

                if (navEarn) {
                    navEarn.onclick = function(e) {
                        e.stopPropagation();
                        closeSidebar();
                        onEarn();
                    };
                }

                var existingBtn = document.getElementById('mtanav-native-hamburger-container');
                if (!existingBtn && window.innerWidth < 1024) {
                    var controlDiv = document.createElement('div');
                    controlDiv.id = 'mtanav-native-hamburger-container';
                    controlDiv.style.margin = '14px';
                    controlDiv.style.cursor = 'pointer';

                    var btn = document.createElement('button');
                    btn.id = 'mtanav-native-hamburger-btn';
                    btn.style.backgroundColor = '#124E4A';
                    btn.style.border = 'none';
                    btn.style.borderRadius = '10px';
                    btn.style.boxShadow = '0 4px 14px rgba(0,0,0,0.25)';
                    btn.style.padding = '10px';
                    btn.style.display = 'flex';
                    btn.style.alignItems = 'center';
                    btn.style.justifyContent = 'center';
                    btn.style.cursor = 'pointer';
                    btn.title = 'Open Navigation Menu';

                    btn.innerHTML = '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#FFFFFF" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="18" x2="21" y2="18"></line></svg>';

                    btn.addEventListener('click', function(e) {
                        e.stopPropagation();
                        openSidebar();
                        onHamb();
                    });

                    controlDiv.appendChild(btn);
                    mapInst.controls[google.maps.ControlPosition.TOP_LEFT].push(controlDiv);
                }
            })()
        """)

        ApiConfig.log("MAP_WEB", "MAP INITIALIZED WITH INTERACTIVE NATIVE HTML SIDEBAR & CONTROL")
    }

    override fun clearMarkers() {
        markersMap.values.forEach { marker ->
            marker.setMap(null)
        }
        markersMap.clear()
        activeInfoWindow?.close()
        activeInfoWindow = null
        ApiConfig.log("MAP_WEB", "MAP MARKERS CLEARED")
    }

    override fun showBusinesses(businesses: List<MapBusiness>) {
        clearMarkers()

        val validBusinesses = businesses.filter { it.hasValidCoordinates() }
        if (validBusinesses.isEmpty() || jsMap == null) return

        val mapInstance = jsMap
        val bounds = js("new google.maps.LatLngBounds()")

        validBusinesses.forEach { business ->
            val bLat = business.latitude
            val bLng = business.longitude
            val bName = business.name
            val bId = business.id
            val bRating = business.rating
            val bReviews = business.reviewCount
            val bAddress = business.address

            js("bounds.extend({ lat: bLat, lng: bLng })")

            val matchHtml = if (business.matchingProduct != null) {
                "<div style='background:#E4F3F0; color:#176B65; font-weight:700; font-size:12px; padding:4px 8px; border-radius:6px; margin-bottom:8px;'>Item: ${business.matchingProduct}${if (business.price != null) " • TSh " + business.price.toInt() else ""}</div>"
            } else if (business.matchingService != null) {
                "<div style='background:#E4F3F0; color:#176B65; font-weight:700; font-size:12px; padding:4px 8px; border-radius:6px; margin-bottom:8px;'>Service: ${business.matchingService}</div>"
            } else ""

            val infoHtml = """
                <div style="padding:10px; font-family:-apple-system,BlinkMacSystemFont,sans-serif; color:#0F172A; max-width:260px;">
                    <div style="font-weight:800; font-size:15px; margin-bottom:4px; color:#124E4A;">$bName</div>
                    <div style="color:#64748B; font-size:12px; margin-bottom:6px;">⭐ $bRating ($bReviews) • $bAddress</div>
                    $matchHtml
                    <div style="display:flex; gap:8px; margin-top:10px;">
                        <button id="mtanav-btn-view-$bId" style="flex:1; background-color:#124E4A; color:white; border:none; padding:8px 12px; border-radius:6px; font-size:12px; font-weight:800; cursor:pointer;">VIEW SHOP</button>
                        <button id="mtanav-btn-nav-$bId" style="flex:1; background-color:#E8AD4A; color:#124E4A; border:none; padding:8px 12px; border-radius:6px; font-size:12px; font-weight:800; cursor:pointer;">NAVIGATE</button>
                    </div>
                </div>
            """.trimIndent()

            val marker = js("""
                new google.maps.Marker({
                    position: { lat: bLat, lng: bLng },
                    map: mapInstance,
                    title: bName,
                    icon: {
                        path: google.maps.SymbolPath.CIRCLE,
                        scale: 8,
                        fillColor: "#124E4A",
                        fillOpacity: 1,
                        strokeWeight: 2,
                        strokeColor: "#E8AD4A"
                    }
                })
            """)

            val infoWindow = js("new google.maps.InfoWindow({ content: infoHtml })")

            marker.addListener("click", {
                activeInfoWindow?.close()
                infoWindow.open(mapInstance, marker)
                activeInfoWindow = infoWindow
                onBusinessSelected(business)

                window.setTimeout({
                    val viewBtn = document.getElementById("mtanav-btn-view-$bId")
                    viewBtn?.addEventListener("click", {
                        onViewShopClicked(bId)
                    })

                    val navBtn = document.getElementById("mtanav-btn-nav-$bId")
                    navBtn?.addEventListener("click", {
                        onNavigateClicked(business)
                    })
                }, 100)
            })

            markersMap[business.id] = marker
        }

        ApiConfig.log("MAP_WEB", "MAP MARKERS ADDED: ${validBusinesses.size}")

        if (validBusinesses.size == 1) {
            val first = validBusinesses.first()
            val fLat = first.latitude
            val fLng = first.longitude
            js("mapInstance.setCenter({ lat: fLat, lng: fLng })")
            js("mapInstance.setZoom(15)")
        } else if (validBusinesses.size > 1) {
            js("mapInstance.fitBounds(bounds)")
            ApiConfig.log("MAP_WEB", "MAP FIT BOUNDS")
        }
    }

    override fun focusBusiness(business: MapBusiness) {
        if (!business.hasValidCoordinates() || jsMap == null) return
        val mapInstance = jsMap
        val bLat = business.latitude
        val bLng = business.longitude
        js("mapInstance.setCenter({ lat: bLat, lng: bLng })")
        js("mapInstance.setZoom(16)")
        showBusinessInfo(business)
    }

    override fun fitBusinesses(businesses: List<MapBusiness>) {
        val valid = businesses.filter { it.hasValidCoordinates() }
        if (valid.isEmpty() || jsMap == null) return
        if (valid.size == 1) {
            focusBusiness(valid.first())
            return
        }
        val mapInstance = jsMap
        val bounds = js("new google.maps.LatLngBounds()")
        valid.forEach { b ->
            val lat = b.latitude
            val lng = b.longitude
            js("bounds.extend({ lat: lat, lng: lng })")
        }
        js("mapInstance.fitBounds(bounds)")
    }

    override fun moveCamera(latitude: Double, longitude: Double, zoom: Float) {
        if (jsMap == null) return
        val mapInstance = jsMap
        js("mapInstance.setCenter({ lat: latitude, lng: longitude })")
        js("mapInstance.setZoom(zoom)")
    }

    override fun showBusinessInfo(business: MapBusiness) {
        val targetMarker = markersMap[business.id] ?: return
        js("google.maps.event.trigger(targetMarker, 'click')")
    }

    override fun clearSelection() {
        activeInfoWindow?.close()
        activeInfoWindow = null
    }

    override fun destroy() {
        clearMarkers()
        jsMap = null
    }
}
