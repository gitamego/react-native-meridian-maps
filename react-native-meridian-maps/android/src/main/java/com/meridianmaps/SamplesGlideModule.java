package com.meridianmaps;


import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;


/**
 * Creates an AppGlideModule that is needed by glide within the Meridian SDK for mapSheetFragment.
 */
@GlideModule
public class SamplesGlideModule extends AppGlideModule {
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
