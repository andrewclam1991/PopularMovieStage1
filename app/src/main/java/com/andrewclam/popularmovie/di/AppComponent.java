package com.andrewclam.popularmovie.di;

import android.app.Application;
import android.support.annotation.NonNull;


import com.andrewclam.popularmovie.MyApplication;
import com.andrewclam.popularmovie.data.MovieRepositoryModule;
import com.andrewclam.popularmovie.di.modules.ActivityBindingModule;
import com.andrewclam.popularmovie.di.modules.ApplicationModule;
import com.andrewclam.popularmovie.di.modules.ContentResolverModule;
import com.andrewclam.popularmovie.di.modules.SchedulerProviderModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * This is a Dagger component. Refer to {@link MyApplication} for the list of Dagger components
 * used in this application.
 * <p>
 * Even though Dagger allows annotating a {@link Component} as a singleton, the code
 * itself must ensure only one instance of the class is created. This is done in {@link
 * MyApplication}.
 * <p>
 * {@link AndroidSupportInjectionModule}
 * is the module from Dagger.Android that helps with the generation
 * and location of subcomponents.
 */
@Singleton
@Component(modules = {
    // Base modules
    AndroidSupportInjectionModule.class,
    ApplicationModule.class,
    ActivityBindingModule.class,

    // RxJava modules
    SchedulerProviderModule.class,

    // Persistence modules
    ContentResolverModule.class,
    MovieRepositoryModule.class})
public interface AppComponent extends AndroidInjector<DaggerApplication> {

  void inject(MyApplication application);

  @Override
  void inject(DaggerApplication instance);

  // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).
  // buildAs().inject(this); never having to instantiate any modules or say which module we are
  // passing the application to. Application will just be provided into our app graph now.
  @Component.Builder
  interface Builder {

    @BindsInstance
    AppComponent.Builder application(Application application);

    @NonNull
    AppComponent build();
  }
}
