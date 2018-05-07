package com.andrewclam.popularmovie.di.modules;

import android.support.annotation.NonNull;


import com.andrewclam.popularmovie.di.annotations.ActivityScoped;
import com.andrewclam.popularmovie.views.detail.DetailActivity;
import com.andrewclam.popularmovie.views.detail.DetailModule;
import com.andrewclam.popularmovie.views.main.MainActivity;
import com.andrewclam.popularmovie.views.main.MainModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever
 * module ActivityBindingModule is on, in our case that will be AppComponent.
 * The beautiful part about this setup is that you never need to tell AppComponent that
 * it is going to have all these subcomponents nor do you need to tell these subcomponents
 * that AppComponent exists.
 * <p>
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation @ActivityScoped
 * When Dagger.Android annotation processor runs it will create the subcomponents for us.
 */
@Module
public abstract class ActivityBindingModule {
  @NonNull
  @ActivityScoped
  @ContributesAndroidInjector(modules = MainModule.class)
  abstract MainActivity mainActivity();

  @NonNull
  @ActivityScoped
  @ContributesAndroidInjector(modules = DetailModule.class)
  abstract DetailActivity detailActivity();
}


