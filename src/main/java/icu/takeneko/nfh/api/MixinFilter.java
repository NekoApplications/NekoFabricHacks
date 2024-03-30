package icu.takeneko.nfh.api;

public interface MixinFilter {
    boolean onMixinApply(String mixinClassName, String mixinConfigName, String targetClassName, String stage);
}
