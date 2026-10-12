(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', './kotlin-kotlin-stdlib.js', './compose-multiplatform-core-compose-runtime-runtime.js'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('./kotlin-kotlin-stdlib.js'), require('./compose-multiplatform-core-compose-runtime-runtime.js'));
  else {
    if (typeof this['kotlin-kotlin-stdlib'] === 'undefined') {
      throw new Error("Error loading module 'compose-multiplatform-core-compose-runtime-runtime-saveable'. Its dependency 'kotlin-kotlin-stdlib' was not found. Please, check whether 'kotlin-kotlin-stdlib' is loaded prior to 'compose-multiplatform-core-compose-runtime-runtime-saveable'.");
    }
    if (typeof this['compose-multiplatform-core-compose-runtime-runtime'] === 'undefined') {
      throw new Error("Error loading module 'compose-multiplatform-core-compose-runtime-runtime-saveable'. Its dependency 'compose-multiplatform-core-compose-runtime-runtime' was not found. Please, check whether 'compose-multiplatform-core-compose-runtime-runtime' is loaded prior to 'compose-multiplatform-core-compose-runtime-runtime-saveable'.");
    }
    root['compose-multiplatform-core-compose-runtime-runtime-saveable'] = factory(typeof this['compose-multiplatform-core-compose-runtime-runtime-saveable'] === 'undefined' ? {} : this['compose-multiplatform-core-compose-runtime-runtime-saveable'], this['kotlin-kotlin-stdlib'], this['compose-multiplatform-core-compose-runtime-runtime']);
  }
}(this, function (_, kotlin_kotlin, kotlin_org_jetbrains_compose_runtime_runtime) {
  'use strict';
  //region block: imports
  var THROW_CCE = kotlin_kotlin.$_$.pk;
  var toString = kotlin_kotlin.$_$.lf;
  var IllegalArgumentException_init_$Create$ = kotlin_kotlin.$_$.z1;
  var Unit_getInstance = kotlin_kotlin.$_$.t5;
  var ArrayList_init_$Create$ = kotlin_kotlin.$_$.p;
  var sourceInformation = kotlin_org_jetbrains_compose_runtime_runtime.$_$.z1;
  var traceEventStart = kotlin_org_jetbrains_compose_runtime_runtime.$_$.d2;
  var isTraceInProgress = kotlin_org_jetbrains_compose_runtime_runtime.$_$.k1;
  var get_currentCompositeKeyHash = kotlin_org_jetbrains_compose_runtime_runtime.$_$.d1;
  var charSequenceLength = kotlin_kotlin.$_$.sd;
  var toString_0 = kotlin_kotlin.$_$.jj;
  var isInterface = kotlin_kotlin.$_$.te;
  var sourceInformationMarkerStart = kotlin_org_jetbrains_compose_runtime_runtime.$_$.y1;
  var sourceInformationMarkerEnd = kotlin_org_jetbrains_compose_runtime_runtime.$_$.x1;
  var Companion_getInstance = kotlin_org_jetbrains_compose_runtime_runtime.$_$.m2;
  var SideEffect = kotlin_org_jetbrains_compose_runtime_runtime.$_$.w;
  var traceEventEnd = kotlin_org_jetbrains_compose_runtime_runtime.$_$.c2;
  var protoOf = kotlin_kotlin.$_$.gf;
  var contentEquals = kotlin_kotlin.$_$.w7;
  var RememberObserver = kotlin_org_jetbrains_compose_runtime_runtime.$_$.u;
  var classMeta = kotlin_kotlin.$_$.ud;
  var setMetadataFor = kotlin_kotlin.$_$.hf;
  var VOID = kotlin_kotlin.$_$.h;
  var toString_1 = kotlin_kotlin.$_$.em;
  var referentialEqualityPolicy = kotlin_org_jetbrains_compose_runtime_runtime.$_$.s1;
  var structuralEqualityPolicy = kotlin_org_jetbrains_compose_runtime_runtime.$_$.b2;
  var neverEqualPolicy = kotlin_org_jetbrains_compose_runtime_runtime.$_$.r1;
  var SnapshotMutableState = kotlin_org_jetbrains_compose_runtime_runtime.$_$.d;
  var interfaceMeta = kotlin_kotlin.$_$.ke;
  var toMutableMap = kotlin_kotlin.$_$.cc;
  var objectMeta = kotlin_kotlin.$_$.ff;
  var DisposableEffectResult = kotlin_org_jetbrains_compose_runtime_runtime.$_$.p;
  var Map = kotlin_kotlin.$_$.o6;
  var updateChangedFlags = kotlin_org_jetbrains_compose_runtime_runtime.$_$.e2;
  var LinkedHashMap_init_$Create$ = kotlin_kotlin.$_$.y;
  var get_reuseKey = kotlin_org_jetbrains_compose_runtime_runtime.$_$.v1;
  var CompositionLocalProvider = kotlin_org_jetbrains_compose_runtime_runtime.$_$.l;
  var DisposableEffect = kotlin_org_jetbrains_compose_runtime_runtime.$_$.q;
  var isBlank = kotlin_kotlin.$_$.vh;
  var ArrayList_init_$Create$_0 = kotlin_kotlin.$_$.o;
  var ArrayList_init_$Create$_1 = kotlin_kotlin.$_$.n;
  var IllegalStateException_init_$Create$ = kotlin_kotlin.$_$.f2;
  var arrayListOf = kotlin_kotlin.$_$.b7;
  var staticCompositionLocalOf = kotlin_org_jetbrains_compose_runtime_runtime.$_$.a2;
  //endregion
  //region block: pre-declaration
  setMetadataFor(SaverScope, 'SaverScope', interfaceMeta);
  setMetadataFor(SaveableHolder, 'SaveableHolder', classMeta, VOID, [SaverScope, RememberObserver]);
  setMetadataFor(SaveableStateHolder, 'SaveableStateHolder', interfaceMeta);
  setMetadataFor(RegistryHolder, 'RegistryHolder', classMeta);
  setMetadataFor(Companion, 'Companion', objectMeta);
  setMetadataFor(_no_name_provided__qut3iv, VOID, classMeta, VOID, [DisposableEffectResult]);
  setMetadataFor(SaveableStateHolderImpl, 'SaveableStateHolderImpl', classMeta, VOID, [SaveableStateHolder], SaveableStateHolderImpl);
  setMetadataFor(Entry, 'Entry', interfaceMeta);
  setMetadataFor(SaveableStateRegistry, 'SaveableStateRegistry', interfaceMeta);
  setMetadataFor(SaveableStateRegistryImpl$registerProvider$3, VOID, classMeta, VOID, [Entry]);
  setMetadataFor(SaveableStateRegistryImpl, 'SaveableStateRegistryImpl', classMeta, VOID, [SaveableStateRegistry]);
  setMetadataFor(Saver, 'Saver', interfaceMeta);
  setMetadataFor(Saver$1, VOID, classMeta, VOID, [Saver]);
  //endregion
  function listSaver(save, restore) {
    var tmp = listSaver$lambda(save);
    return Saver_0(tmp, typeof restore === 'function' ? restore : THROW_CCE());
  }
  function listSaver$lambda($save) {
    return function ($this$Saver, it) {
      var list = $save($this$Saver, it);
      var inductionVariable = 0;
      var last = list.get_size_woubt6_k$() - 1 | 0;
      var tmp;
      if (inductionVariable <= last) {
        do {
          var index = inductionVariable;
          inductionVariable = inductionVariable + 1 | 0;
          var item = list.get_c1px32_k$(index);
          if (!(item == null)) {
            // Inline function 'kotlin.require' call
            // Inline function 'kotlin.contracts.contract' call
            if (!$this$Saver.canBeSaved_nnajrq_k$(item)) {
              // Inline function 'androidx.compose.runtime.saveable.listSaver.<anonymous>.<anonymous>' call
              var message = "item can't be saved";
              throw IllegalArgumentException_init_$Create$(toString(message));
            }
          }
        }
         while (inductionVariable <= last);
        tmp = Unit_getInstance();
      }
      var tmp_0;
      // Inline function 'kotlin.collections.isNotEmpty' call
      if (!list.isEmpty_y1axqb_k$()) {
        tmp_0 = ArrayList_init_$Create$(list);
      } else {
        tmp_0 = null;
      }
      return tmp_0;
    };
  }
  function get_MaxSupportedRadix() {
    return MaxSupportedRadix;
  }
  var MaxSupportedRadix;
  function rememberSaveable(inputs, saver, key, init, $composer, $changed, $default) {
    var saver_0 = {_v: saver};
    var key_0 = key;
    var $composer_0 = $composer;
    $composer_0.startReplaceableGroup_ip860b_k$(441892779);
    sourceInformation($composer_0, 'C(rememberSaveable)P(1,3,2)71@3180L23,81@3526L7,83@3552L313,93@3940L82:RememberSaveable.kt#r2ddri');
    if (!(($default & 2) === 0)) {
      saver_0._v = autoSaver();
    }
    if (!(($default & 4) === 0))
      key_0 = null;
    if (isTraceInProgress()) {
      traceEventStart(441892779, $changed, -1, 'androidx.compose.runtime.saveable.rememberSaveable (RememberSaveable.kt:70)');
    }
    var compositeKey = get_currentCompositeKeyHash($composer_0, 0);
    var tmp;
    // Inline function 'kotlin.text.isNullOrEmpty' call
    var this_0 = key_0;
    // Inline function 'kotlin.contracts.contract' call
    if (!(this_0 == null ? true : charSequenceLength(this_0) === 0)) {
      tmp = key_0;
    } else {
      tmp = toString_0(compositeKey, MaxSupportedRadix);
    }
    var finalKey = tmp;
    var tmp_0 = saver_0._v;
    if (!(!(tmp_0 == null) ? isInterface(tmp_0, Saver) : false))
      THROW_CCE();
    // Inline function 'androidx.compose.runtime.CompositionLocal.current' call
    var this_1 = get_LocalSaveableStateRegistry();
    var $composer_1 = $composer_0;
    sourceInformationMarkerStart($composer_1, 2023513938, 'CC:CompositionLocal.kt#9igjgp');
    var tmp0 = $composer_1.consume_ebzcrh_k$(this_1);
    sourceInformationMarkerEnd($composer_1);
    var registry = tmp0;
    $composer_0.startReplaceableGroup_ip860b_k$(1059366748);
    sourceInformation($composer_0, 'CC(remember):RememberSaveable.kt#9igjgp');
    // Inline function 'androidx.compose.runtime.cache' call
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'androidx.compose.runtime.cache.<anonymous>' call
    var it = $composer_0.rememberedValue_4dg93v_k$();
    var tmp_1;
    if (false ? true : it === Companion_getInstance().get_Empty_i9b85g_k$()) {
      // Inline function 'androidx.compose.runtime.saveable.rememberSaveable.<anonymous>' call
      var tmp1_safe_receiver = registry == null ? null : registry.consumeRestored_yfjzaw_k$(finalKey);
      var tmp_2;
      if (tmp1_safe_receiver == null) {
        tmp_2 = null;
      } else {
        // Inline function 'kotlin.let' call
        // Inline function 'kotlin.contracts.contract' call
        // Inline function 'androidx.compose.runtime.saveable.rememberSaveable.<anonymous>.<anonymous>' call
        tmp_2 = saver_0._v.restore_fyjq85_k$(tmp1_safe_receiver);
      }
      var restored = tmp_2;
      var finalValue = restored == null ? init() : restored;
      var value = new SaveableHolder(saver_0._v, registry, finalKey, finalValue, inputs);
      $composer_0.updateRememberedValue_l1wh71_k$(value);
      tmp_1 = value;
    } else {
      tmp_1 = it;
    }
    var tmp_3 = tmp_1;
    var tmp1_group = (tmp_3 == null ? true : !(tmp_3 == null)) ? tmp_3 : THROW_CCE();
    $composer_0.endReplaceableGroup_ern0ak_k$();
    var holder = tmp1_group;
    var tmp0_elvis_lhs = holder.getValueIfInputsDidntChange_2my39q_k$(inputs);
    var value_0 = tmp0_elvis_lhs == null ? init() : tmp0_elvis_lhs;
    SideEffect(rememberSaveable$lambda(holder, saver_0, registry, finalKey, value_0, inputs), $composer_0, 0);
    if (isTraceInProgress()) {
      traceEventEnd();
    }
    $composer_0.endReplaceableGroup_ern0ak_k$();
    return value_0;
  }
  function _set_saver__ki24i8($this, _set____db54di) {
    $this.saver_1 = _set____db54di;
  }
  function _get_saver__bj2bz8($this) {
    return $this.saver_1;
  }
  function _set_registry__wfdxom($this, _set____db54di) {
    $this.registry_1 = _set____db54di;
  }
  function _get_registry__pststq($this) {
    return $this.registry_1;
  }
  function _set_key__4w8w3q($this, _set____db54di) {
    $this.key_1 = _set____db54di;
  }
  function _get_key__e6bh8y($this) {
    return $this.key_1;
  }
  function _set_value__lx0xdg($this, _set____db54di) {
    $this.value_1 = _set____db54di;
  }
  function _get_value__a43j40($this) {
    return $this.value_1;
  }
  function _set_inputs__2dcq7m($this, _set____db54di) {
    $this.inputs_1 = _set____db54di;
  }
  function _get_inputs__wm5ku($this) {
    return $this.inputs_1;
  }
  function _set_entry__e2jqj7($this, _set____db54di) {
    $this.entry_1 = _set____db54di;
  }
  function _get_entry__hykpy9($this) {
    return $this.entry_1;
  }
  function _get_valueProvider__r2iult($this) {
    return $this.valueProvider_1;
  }
  function register($this) {
    var registry = $this.registry_1;
    // Inline function 'kotlin.require' call
    // Inline function 'kotlin.contracts.contract' call
    if (!($this.entry_1 == null)) {
      // Inline function 'androidx.compose.runtime.saveable.SaveableHolder.register.<anonymous>' call
      var message = 'entry(' + $this.entry_1 + ') is not null';
      throw IllegalArgumentException_init_$Create$(toString(message));
    }
    if (!(registry == null)) {
      requireCanBeSaved(registry, $this.valueProvider_1());
      $this.entry_1 = registry.registerProvider_22k33k_k$($this.key_1, $this.valueProvider_1);
    }
  }
  function SaveableHolder$valueProvider$lambda(this$0) {
    return function () {
      // Inline function 'kotlin.with' call
      // Inline function 'kotlin.contracts.contract' call
      // Inline function 'androidx.compose.runtime.saveable.SaveableHolder.valueProvider.<anonymous>.<anonymous>' call
      var $this$with = this$0.saver_1;
      var tmp$ret$1;
      $l$block: {
        // Inline function 'kotlin.requireNotNull' call
        var value = this$0.value_1;
        // Inline function 'kotlin.contracts.contract' call
        if (value == null) {
          // Inline function 'androidx.compose.runtime.saveable.SaveableHolder.valueProvider.<anonymous>.<anonymous>.<anonymous>' call
          var message = 'Value should be initialized';
          throw IllegalArgumentException_init_$Create$(toString(message));
        } else {
          tmp$ret$1 = value;
          break $l$block;
        }
      }
      return $this$with.save_khi2yu_k$(this$0, tmp$ret$1);
    };
  }
  function SaveableHolder(saver, registry, key, value, inputs) {
    this.saver_1 = saver;
    this.registry_1 = registry;
    this.key_1 = key;
    this.value_1 = value;
    this.inputs_1 = inputs;
    this.entry_1 = null;
    var tmp = this;
    tmp.valueProvider_1 = SaveableHolder$valueProvider$lambda(this);
  }
  protoOf(SaveableHolder).update_yik83o_k$ = function (saver, registry, key, value, inputs) {
    var entryIsOutdated = false;
    if (!(this.registry_1 === registry)) {
      this.registry_1 = registry;
      entryIsOutdated = true;
    }
    if (!(this.key_1 === key)) {
      this.key_1 = key;
      entryIsOutdated = true;
    }
    this.saver_1 = saver;
    this.value_1 = value;
    this.inputs_1 = inputs;
    if (!(this.entry_1 == null) ? entryIsOutdated : false) {
      var tmp0_safe_receiver = this.entry_1;
      if (tmp0_safe_receiver == null)
        null;
      else {
        tmp0_safe_receiver.unregister_ib976c_k$();
      }
      this.entry_1 = null;
      register(this);
    }
  };
  protoOf(SaveableHolder).canBeSaved_nnajrq_k$ = function (value) {
    var registry = this.registry_1;
    return registry == null ? true : registry.canBeSaved_nnajrq_k$(value);
  };
  protoOf(SaveableHolder).onRemembered_68t2e3_k$ = function () {
    register(this);
  };
  protoOf(SaveableHolder).onForgotten_pbqifp_k$ = function () {
    var tmp0_safe_receiver = this.entry_1;
    if (tmp0_safe_receiver == null)
      null;
    else {
      tmp0_safe_receiver.unregister_ib976c_k$();
    }
  };
  protoOf(SaveableHolder).onAbandoned_1gx7a3_k$ = function () {
    var tmp0_safe_receiver = this.entry_1;
    if (tmp0_safe_receiver == null)
      null;
    else {
      tmp0_safe_receiver.unregister_ib976c_k$();
    }
  };
  protoOf(SaveableHolder).getValueIfInputsDidntChange_2my39q_k$ = function (inputs) {
    var tmp;
    if (contentEquals(inputs, this.inputs_1)) {
      tmp = this.value_1;
    } else {
      tmp = null;
    }
    return tmp;
  };
  function requireCanBeSaved(_this__u8e3s4, value) {
    if (!(value == null) ? !_this__u8e3s4.canBeSaved_nnajrq_k$(value) : false) {
      var tmp;
      if (!(value == null) ? isInterface(value, SnapshotMutableState) : false) {
        var tmp_0;
        if ((!(value.get_policy_i5q4hn_k$() === neverEqualPolicy()) ? !(value.get_policy_i5q4hn_k$() === structuralEqualityPolicy()) : false) ? !(value.get_policy_i5q4hn_k$() === referentialEqualityPolicy()) : false) {
          tmp_0 = 'If you use a custom SnapshotMutationPolicy for your MutableState you have to write a custom Saver';
        } else {
          tmp_0 = 'MutableState containing ' + toString_1(value.get_value_j01efc_k$()) + ' cannot be saved using the current ' + 'SaveableStateRegistry. The default implementation only supports types ' + 'which can be stored inside the Bundle. Please consider implementing a ' + 'custom Saver for this class and pass it as a stateSaver parameter to ' + 'rememberSaveable().';
        }
        tmp = tmp_0;
      } else {
        tmp = toString_1(value) + ' cannot be saved using the current SaveableStateRegistry. The default ' + 'implementation only supports types which can be stored inside the Bundle' + '. Please consider implementing a custom Saver for this class and pass it' + ' to rememberSaveable().';
      }
      throw IllegalArgumentException_init_$Create$(tmp);
    }
  }
  function rememberSaveable$lambda($holder, $saver, $registry, $finalKey, $value, $inputs) {
    return function () {
      $holder.update_yik83o_k$($saver._v, $registry, $finalKey, $value, $inputs);
      return Unit_getInstance();
    };
  }
  function SaveableStateHolder() {
  }
  function rememberSaveableStateHolder($composer, $changed) {
    var $composer_0 = $composer;
    $composer_0.startReplaceableGroup_ip860b_k$(15454635);
    sourceInformation($composer_0, 'C(rememberSaveableStateHolder)*61@2439L41,59@2369L111,64@2554L7:SaveableStateHolder.kt#r2ddri');
    if (isTraceInProgress()) {
      traceEventStart(15454635, $changed, -1, 'androidx.compose.runtime.saveable.rememberSaveableStateHolder (SaveableStateHolder.kt:59)');
    }
    // Inline function 'kotlin.apply' call
    var tmp = Companion_getInstance_0().Saver_1;
    $composer_0.startReplaceableGroup_ip860b_k$(-579869522);
    sourceInformation($composer_0, 'CC(remember):SaveableStateHolder.kt#9igjgp');
    // Inline function 'androidx.compose.runtime.cache' call
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'androidx.compose.runtime.cache.<anonymous>' call
    var it = $composer_0.rememberedValue_4dg93v_k$();
    var tmp_0;
    if (false ? true : it === Companion_getInstance().get_Empty_i9b85g_k$()) {
      // Inline function 'androidx.compose.runtime.saveable.rememberSaveableStateHolder.<anonymous>' call
      var value = rememberSaveableStateHolder$lambda;
      $composer_0.updateRememberedValue_l1wh71_k$(value);
      tmp_0 = value;
    } else {
      tmp_0 = it;
    }
    var tmp_1 = tmp_0;
    var tmp1_group = (tmp_1 == null ? true : !(tmp_1 == null)) ? tmp_1 : THROW_CCE();
    $composer_0.endReplaceableGroup_ern0ak_k$();
    var this_0 = rememberSaveable([], tmp, null, tmp1_group, $composer_0, 3080, 4);
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'androidx.compose.runtime.saveable.rememberSaveableStateHolder.<anonymous>' call
    var tmp_2 = this_0;
    // Inline function 'androidx.compose.runtime.CompositionLocal.current' call
    var this_1 = get_LocalSaveableStateRegistry();
    var $composer_1 = $composer_0;
    sourceInformationMarkerStart($composer_1, 2023513938, 'CC:CompositionLocal.kt#9igjgp');
    var tmp0 = $composer_1.consume_ebzcrh_k$(this_1);
    sourceInformationMarkerEnd($composer_1);
    tmp_2.parentSaveableStateRegistry_1 = tmp0;
    if (isTraceInProgress()) {
      traceEventEnd();
    }
    $composer_0.endReplaceableGroup_ern0ak_k$();
    return this_0;
  }
  function SaveableStateHolderImpl$RegistryHolder$registry$lambda(this$0) {
    return function (it) {
      var tmp0_safe_receiver = this$0.parentSaveableStateRegistry_1;
      var tmp1_elvis_lhs = tmp0_safe_receiver == null ? null : tmp0_safe_receiver.canBeSaved_nnajrq_k$(it);
      return tmp1_elvis_lhs == null ? true : tmp1_elvis_lhs;
    };
  }
  function SaveableStateHolderImpl$Companion$Saver$lambda($this$Saver, it) {
    return saveAll(it);
  }
  function SaveableStateHolderImpl$Companion$Saver$lambda_0(it) {
    return new SaveableStateHolderImpl(it);
  }
  function _get_savedStates__8lkyig($this) {
    return $this.savedStates_1;
  }
  function _get_registryHolders__cest15($this) {
    return $this.registryHolders_1;
  }
  function saveAll($this) {
    var map = toMutableMap($this.savedStates_1);
    // Inline function 'kotlin.collections.forEach' call
    var tmp0_iterator = $this.registryHolders_1.get_values_ksazhn_k$().iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var element = tmp0_iterator.next_20eer_k$();
      // Inline function 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.saveAll.<anonymous>' call
      element.saveTo_mv4zhb_k$(map);
    }
    // Inline function 'kotlin.collections.ifEmpty' call
    var tmp;
    if (map.isEmpty_y1axqb_k$()) {
      // Inline function 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.saveAll.<anonymous>' call
      tmp = null;
    } else {
      tmp = map;
    }
    return tmp;
  }
  function RegistryHolder($outer, key) {
    this.$this_1 = $outer;
    this.key_1 = key;
    this.shouldSave_1 = true;
    var tmp = this;
    var tmp_0 = this.$this_1.savedStates_1.get_wei43m_k$(this.key_1);
    tmp.registry_1 = SaveableStateRegistry_0(tmp_0, SaveableStateHolderImpl$RegistryHolder$registry$lambda(this.$this_1));
  }
  protoOf(RegistryHolder).get_key_18j28a_k$ = function () {
    return this.key_1;
  };
  protoOf(RegistryHolder).set_shouldSave_68lx7c_k$ = function (_set____db54di) {
    this.shouldSave_1 = _set____db54di;
  };
  protoOf(RegistryHolder).get_shouldSave_p014mf_k$ = function () {
    return this.shouldSave_1;
  };
  protoOf(RegistryHolder).get_registry_rt8npi_k$ = function () {
    return this.registry_1;
  };
  protoOf(RegistryHolder).saveTo_mv4zhb_k$ = function (map) {
    if (this.shouldSave_1) {
      var savedData = this.registry_1.performSave_ohn24i_k$();
      if (savedData.isEmpty_y1axqb_k$()) {
        // Inline function 'kotlin.collections.minusAssign' call
        var key = this.key_1;
        map.remove_gppy8k_k$(key);
      } else {
        // Inline function 'kotlin.collections.set' call
        var key_0 = this.key_1;
        map.put_4fpzoq_k$(key_0, savedData);
      }
    }
  };
  function Companion() {
    Companion_instance = this;
    var tmp = this;
    var tmp_0 = SaveableStateHolderImpl$Companion$Saver$lambda;
    tmp.Saver_1 = Saver_0(tmp_0, SaveableStateHolderImpl$Companion$Saver$lambda_0);
  }
  protoOf(Companion).get_Saver_igssp8_k$ = function () {
    return this.Saver_1;
  };
  var Companion_instance;
  function Companion_getInstance_0() {
    if (Companion_instance == null)
      new Companion();
    return Companion_instance;
  }
  function _no_name_provided__qut3iv($registryHolder, this$0, $key) {
    this.$registryHolder_1 = $registryHolder;
    this.this$0__1 = this$0;
    this.$key_1 = $key;
  }
  protoOf(_no_name_provided__qut3iv).dispose_3nnxhr_k$ = function () {
    // Inline function 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.SaveableStateProvider.<anonymous>.<anonymous>.<anonymous>' call
    this.$registryHolder_1.saveTo_mv4zhb_k$(this.this$0__1.savedStates_1);
    // Inline function 'kotlin.collections.minusAssign' call
    var this_0 = this.this$0__1.registryHolders_1;
    var key = this.$key_1;
    this_0.remove_gppy8k_k$(key);
  };
  function SaveableStateHolderImpl$SaveableStateProvider$lambda(this$0, $key, $registryHolder) {
    return function ($this$DisposableEffect) {
      // Inline function 'kotlin.collections.contains' call
      var this_0 = this$0.registryHolders_1;
      // Inline function 'kotlin.collections.containsKey' call
      var key = $key;
      // Inline function 'kotlin.contracts.contract' call
      var tmp;
      if (!!(isInterface(this_0, Map) ? this_0 : THROW_CCE()).containsKey_aw81wo_k$(key)) {
        // Inline function 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.SaveableStateProvider.<anonymous>.<anonymous>.<anonymous>' call
        var message = 'Key ' + toString($key) + ' was used multiple times ';
        throw IllegalArgumentException_init_$Create$(toString(message));
      }
      var this_1 = this$0.savedStates_1;
      var key_0 = $key;
      this_1.remove_gppy8k_k$(key_0);
      var this_2 = this$0.registryHolders_1;
      var key_1 = $key;
      var value = $registryHolder;
      this_2.put_4fpzoq_k$(key_1, value);
      // Inline function 'androidx.compose.runtime.DisposableEffectScope.onDispose' call
      return new _no_name_provided__qut3iv($registryHolder, this$0, $key);
    };
  }
  function SaveableStateHolderImpl$SaveableStateProvider$lambda_0($tmp0_rcvr, $key, $content, $$changed) {
    return function ($composer, $force) {
      $tmp0_rcvr.SaveableStateProvider_3h7wny_k$($key, $content, $composer, updateChangedFlags($$changed | 1));
      return Unit_getInstance();
    };
  }
  function SaveableStateHolderImpl(savedStates) {
    Companion_getInstance_0();
    var tmp;
    if (savedStates === VOID) {
      // Inline function 'kotlin.collections.mutableMapOf' call
      tmp = LinkedHashMap_init_$Create$();
    } else {
      tmp = savedStates;
    }
    savedStates = tmp;
    this.savedStates_1 = savedStates;
    var tmp_0 = this;
    // Inline function 'kotlin.collections.mutableMapOf' call
    tmp_0.registryHolders_1 = LinkedHashMap_init_$Create$();
    this.parentSaveableStateRegistry_1 = null;
  }
  protoOf(SaveableStateHolderImpl).set_parentSaveableStateRegistry_rshrfm_k$ = function (_set____db54di) {
    this.parentSaveableStateRegistry_1 = _set____db54di;
  };
  protoOf(SaveableStateHolderImpl).get_parentSaveableStateRegistry_5mprdw_k$ = function () {
    return this.parentSaveableStateRegistry_1;
  };
  protoOf(SaveableStateHolderImpl).SaveableStateProvider_3h7wny_k$ = function (key, content, $composer, $changed) {
    var $composer_0 = $composer;
    $composer_0 = $composer_0.startRestartGroup_lebv1i_k$(-1198538093);
    sourceInformation($composer_0, 'C(SaveableStateProvider)P(1)75@2967L923:SaveableStateHolder.kt#r2ddri');
    var $dirty = $changed;
    if (isTraceInProgress()) {
      traceEventStart(-1198538093, $dirty, -1, 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.SaveableStateProvider (SaveableStateHolder.kt:74)');
    }
    // Inline function 'androidx.compose.runtime.ReusableContent' call
    var $composer_1 = $composer_0;
    $composer_1.startReplaceableGroup_ip860b_k$(444418301);
    sourceInformation($composer_1, 'CC(ReusableContent)P(1)145@5313L9:Composables.kt#9igjgp');
    $composer_1.startReusableGroup_1qdhl_k$(get_reuseKey(), key);
    // Inline function 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.SaveableStateProvider.<anonymous>' call
    var $composer_2 = $composer_1;
    sourceInformationMarkerStart($composer_2, 1516495192, 'C76@3023L321,83@3357L150,87@3520L360:SaveableStateHolder.kt#r2ddri');
    $composer_2.startReplaceableGroup_ip860b_k$(1516495213);
    sourceInformation($composer_2, 'CC(remember):SaveableStateHolder.kt#9igjgp');
    // Inline function 'androidx.compose.runtime.cache' call
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'androidx.compose.runtime.cache.<anonymous>' call
    var it = $composer_2.rememberedValue_4dg93v_k$();
    var tmp;
    if (false ? true : it === Companion_getInstance().get_Empty_i9b85g_k$()) {
      // Inline function 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.SaveableStateProvider.<anonymous>.<anonymous>' call
      // Inline function 'kotlin.require' call
      var tmp0_safe_receiver = this.parentSaveableStateRegistry_1;
      var tmp1_elvis_lhs = tmp0_safe_receiver == null ? null : tmp0_safe_receiver.canBeSaved_nnajrq_k$(key);
      // Inline function 'kotlin.contracts.contract' call
      if (!(tmp1_elvis_lhs == null ? true : tmp1_elvis_lhs)) {
        // Inline function 'androidx.compose.runtime.saveable.SaveableStateHolderImpl.SaveableStateProvider.<anonymous>.<anonymous>.<anonymous>' call
        var message = 'Type of the key ' + toString(key) + ' is not supported. On Android you can only use types ' + 'which can be stored inside the Bundle.';
        throw IllegalArgumentException_init_$Create$(toString(message));
      }
      var value = new RegistryHolder(this, key);
      $composer_2.updateRememberedValue_l1wh71_k$(value);
      tmp = value;
    } else {
      tmp = it;
    }
    var tmp_0 = tmp;
    var tmp0_group = (tmp_0 == null ? true : !(tmp_0 == null)) ? tmp_0 : THROW_CCE();
    $composer_2.endReplaceableGroup_ern0ak_k$();
    var registryHolder = tmp0_group;
    CompositionLocalProvider(get_LocalSaveableStateRegistry().provides_3agxel_k$(registryHolder.registry_1), content, $composer_2, 112 & $dirty);
    DisposableEffect(Unit_getInstance(), SaveableStateHolderImpl$SaveableStateProvider$lambda(this, key, registryHolder), $composer_2, 6);
    sourceInformationMarkerEnd($composer_2);
    $composer_1.endReusableGroup_jx82af_k$();
    $composer_1.endReplaceableGroup_ern0ak_k$();
    if (isTraceInProgress()) {
      traceEventEnd();
    }
    var tmp1_safe_receiver = $composer_0.endRestartGroup_yxpjv9_k$();
    if (tmp1_safe_receiver == null)
      null;
    else {
      tmp1_safe_receiver.updateScope_t8jcf_k$(SaveableStateHolderImpl$SaveableStateProvider$lambda_0(this, key, content, $changed));
    }
  };
  protoOf(SaveableStateHolderImpl).removeState_5wfli9_k$ = function (key) {
    var registryHolder = this.registryHolders_1.get_wei43m_k$(key);
    if (!(registryHolder == null)) {
      registryHolder.shouldSave_1 = false;
    } else {
      // Inline function 'kotlin.collections.minusAssign' call
      this.savedStates_1.remove_gppy8k_k$(key);
    }
  };
  function rememberSaveableStateHolder$lambda() {
    return new SaveableStateHolderImpl();
  }
  function get_LocalSaveableStateRegistry() {
    _init_properties_SaveableStateRegistry_kt__lr5zhh();
    return LocalSaveableStateRegistry;
  }
  var LocalSaveableStateRegistry;
  function Entry() {
  }
  function SaveableStateRegistry() {
  }
  function SaveableStateRegistry_0(restoredValues, canBeSaved) {
    _init_properties_SaveableStateRegistry_kt__lr5zhh();
    return new SaveableStateRegistryImpl(restoredValues, canBeSaved);
  }
  function _get_canBeSaved__pps3rd($this) {
    return $this.canBeSaved_1;
  }
  function _get_restored__60hukr($this) {
    return $this.restored_1;
  }
  function _get_valueProviders__d74c8q($this) {
    return $this.valueProviders_1;
  }
  function SaveableStateRegistryImpl$registerProvider$3(this$0, $key, $valueProvider) {
    this.this$0__1 = this$0;
    this.$key_1 = $key;
    this.$valueProvider_1 = $valueProvider;
  }
  protoOf(SaveableStateRegistryImpl$registerProvider$3).unregister_ib976c_k$ = function () {
    var list = this.this$0__1.valueProviders_1.remove_gppy8k_k$(this.$key_1);
    if (list == null)
      null;
    else
      list.remove_cedx0m_k$(this.$valueProvider_1);
    var tmp;
    if (!(list == null)) {
      // Inline function 'kotlin.collections.isNotEmpty' call
      tmp = !list.isEmpty_y1axqb_k$();
    } else {
      tmp = false;
    }
    if (tmp) {
      // Inline function 'kotlin.collections.set' call
      var this_0 = this.this$0__1.valueProviders_1;
      var key = this.$key_1;
      this_0.put_4fpzoq_k$(key, list);
    }
  };
  function SaveableStateRegistryImpl(restored, canBeSaved) {
    this.canBeSaved_1 = canBeSaved;
    var tmp = this;
    var tmp1_elvis_lhs = restored == null ? null : toMutableMap(restored);
    var tmp_0;
    if (tmp1_elvis_lhs == null) {
      // Inline function 'kotlin.collections.mutableMapOf' call
      tmp_0 = LinkedHashMap_init_$Create$();
    } else {
      tmp_0 = tmp1_elvis_lhs;
    }
    tmp.restored_1 = tmp_0;
    var tmp_1 = this;
    // Inline function 'kotlin.collections.mutableMapOf' call
    tmp_1.valueProviders_1 = LinkedHashMap_init_$Create$();
  }
  protoOf(SaveableStateRegistryImpl).canBeSaved_nnajrq_k$ = function (value) {
    return this.canBeSaved_1(value);
  };
  protoOf(SaveableStateRegistryImpl).consumeRestored_yfjzaw_k$ = function (key) {
    var list = this.restored_1.remove_gppy8k_k$(key);
    var tmp;
    var tmp_0;
    if (!(list == null)) {
      // Inline function 'kotlin.collections.isNotEmpty' call
      tmp_0 = !list.isEmpty_y1axqb_k$();
    } else {
      tmp_0 = false;
    }
    if (tmp_0) {
      if (list.get_size_woubt6_k$() > 1) {
        // Inline function 'kotlin.collections.set' call
        var this_0 = this.restored_1;
        var value = list.subList_xle3r2_k$(1, list.get_size_woubt6_k$());
        this_0.put_4fpzoq_k$(key, value);
      }
      tmp = list.get_c1px32_k$(0);
    } else {
      tmp = null;
    }
    return tmp;
  };
  protoOf(SaveableStateRegistryImpl).registerProvider_22k33k_k$ = function (key, valueProvider) {
    // Inline function 'kotlin.require' call
    // Inline function 'kotlin.text.isNotBlank' call
    // Inline function 'kotlin.contracts.contract' call
    if (!!isBlank(key)) {
      // Inline function 'androidx.compose.runtime.saveable.SaveableStateRegistryImpl.registerProvider.<anonymous>' call
      var message = 'Registered key is empty or blank';
      throw IllegalArgumentException_init_$Create$(toString(message));
    }
    // Inline function 'kotlin.collections.getOrPut' call
    var this_0 = this.valueProviders_1;
    var value = this_0.get_wei43m_k$(key);
    var tmp;
    if (value == null) {
      // Inline function 'androidx.compose.runtime.saveable.SaveableStateRegistryImpl.registerProvider.<anonymous>' call
      // Inline function 'kotlin.collections.mutableListOf' call
      var answer = ArrayList_init_$Create$_0();
      this_0.put_4fpzoq_k$(key, answer);
      tmp = answer;
    } else {
      tmp = value;
    }
    tmp.add_utx5q5_k$(valueProvider);
    return new SaveableStateRegistryImpl$registerProvider$3(this, key, valueProvider);
  };
  protoOf(SaveableStateRegistryImpl).performSave_ohn24i_k$ = function () {
    var map = toMutableMap(this.restored_1);
    // Inline function 'kotlin.collections.forEach' call
    // Inline function 'kotlin.collections.iterator' call
    var tmp0_iterator = this.valueProviders_1.get_entries_p20ztl_k$().iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var element = tmp0_iterator.next_20eer_k$();
      // Inline function 'androidx.compose.runtime.saveable.SaveableStateRegistryImpl.performSave.<anonymous>' call
      // Inline function 'kotlin.collections.component1' call
      var key = element.get_key_18j28a_k$();
      // Inline function 'kotlin.collections.component2' call
      var list = element.get_value_j01efc_k$();
      if (list.get_size_woubt6_k$() === 1) {
        var value = list.get_c1px32_k$(0)();
        if (!(value == null)) {
          // Inline function 'kotlin.check' call
          // Inline function 'kotlin.contracts.contract' call
          if (!this.canBeSaved_nnajrq_k$(value)) {
            // Inline function 'androidx.compose.runtime.saveable.SaveableStateRegistryImpl.performSave.<anonymous>.<anonymous>' call
            var message = "item can't be saved";
            throw IllegalStateException_init_$Create$(toString(message));
          }
          // Inline function 'kotlin.collections.set' call
          var value_0 = arrayListOf([value]);
          map.put_4fpzoq_k$(key, value_0);
        }
      } else {
        // Inline function 'kotlin.collections.set' call
        // Inline function 'kotlin.collections.List' call
        // Inline function 'kotlin.collections.MutableList' call
        var size = list.get_size_woubt6_k$();
        var list_0 = ArrayList_init_$Create$_1(size);
        // Inline function 'kotlin.repeat' call
        // Inline function 'kotlin.contracts.contract' call
        var inductionVariable = 0;
        if (inductionVariable < size)
          do {
            var index = inductionVariable;
            inductionVariable = inductionVariable + 1 | 0;
            // Inline function 'kotlin.collections.MutableList.<anonymous>' call
            // Inline function 'androidx.compose.runtime.saveable.SaveableStateRegistryImpl.performSave.<anonymous>.<anonymous>' call
            var value_1 = list.get_c1px32_k$(index)();
            if (!(value_1 == null)) {
              // Inline function 'kotlin.check' call
              // Inline function 'kotlin.contracts.contract' call
              if (!this.canBeSaved_nnajrq_k$(value_1)) {
                // Inline function 'androidx.compose.runtime.saveable.SaveableStateRegistryImpl.performSave.<anonymous>.<anonymous>.<anonymous>' call
                var message_0 = "item can't be saved";
                throw IllegalStateException_init_$Create$(toString(message_0));
              }
            }
            list_0.add_utx5q5_k$(value_1);
          }
           while (inductionVariable < size);
        map.put_4fpzoq_k$(key, list_0);
      }
    }
    return map;
  };
  function LocalSaveableStateRegistry$lambda() {
    _init_properties_SaveableStateRegistry_kt__lr5zhh();
    return null;
  }
  var properties_initialized_SaveableStateRegistry_kt_4lrdzt;
  function _init_properties_SaveableStateRegistry_kt__lr5zhh() {
    if (!properties_initialized_SaveableStateRegistry_kt_4lrdzt) {
      properties_initialized_SaveableStateRegistry_kt_4lrdzt = true;
      LocalSaveableStateRegistry = staticCompositionLocalOf(LocalSaveableStateRegistry$lambda);
    }
  }
  function get_AutoSaver() {
    _init_properties_Saver_kt__z47nhf();
    return AutoSaver;
  }
  var AutoSaver;
  function Saver() {
  }
  function Saver_0(save, restore) {
    _init_properties_Saver_kt__z47nhf();
    return new Saver$1(save, restore);
  }
  function SaverScope() {
  }
  function autoSaver() {
    _init_properties_Saver_kt__z47nhf();
    var tmp = get_AutoSaver();
    return isInterface(tmp, Saver) ? tmp : THROW_CCE();
  }
  function AutoSaver$lambda($this$Saver, it) {
    _init_properties_Saver_kt__z47nhf();
    return it;
  }
  function AutoSaver$lambda_0(it) {
    _init_properties_Saver_kt__z47nhf();
    return it;
  }
  function Saver$1($save, $restore) {
    this.$save_1 = $save;
    this.$restore_1 = $restore;
  }
  protoOf(Saver$1).save_khi2yu_k$ = function (_this__u8e3s4, value) {
    return this.$save_1(_this__u8e3s4, value);
  };
  protoOf(Saver$1).restore_fyjq85_k$ = function (value) {
    return this.$restore_1(value);
  };
  var properties_initialized_Saver_kt_ch40dh;
  function _init_properties_Saver_kt__z47nhf() {
    if (!properties_initialized_Saver_kt_ch40dh) {
      properties_initialized_Saver_kt_ch40dh = true;
      var tmp = AutoSaver$lambda;
      AutoSaver = Saver_0(tmp, AutoSaver$lambda_0);
    }
  }
  //region block: init
  MaxSupportedRadix = 36;
  //endregion
  //region block: exports
  _.$_$ = _.$_$ || {};
  _.$_$.a = get_LocalSaveableStateRegistry;
  _.$_$.b = SaveableStateHolder;
  _.$_$.c = SaveableStateRegistry_0;
  _.$_$.d = SaveableStateRegistry;
  _.$_$.e = Saver_0;
  _.$_$.f = listSaver;
  _.$_$.g = rememberSaveableStateHolder;
  _.$_$.h = rememberSaveable;
  //endregion
  return _;
}));

//# sourceMappingURL=compose-multiplatform-core-compose-runtime-runtime-saveable.js.map
