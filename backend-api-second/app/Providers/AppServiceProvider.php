<?php

namespace App\Providers;

use App\Repositories\Auth\UserRepository;
use App\Repositories\Auth\UserRepositoryInterface;
use App\Services\Auth\AuthService;
use App\Services\Auth\AuthFacade;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        // Register Repositories
        $this->app->bind(UserRepositoryInterface::class, UserRepository::class);

        // Register Services
        $this->app->singleton('auth.service', function ($app) {
            return new AuthService(
                $app->make(UserRepositoryInterface::class)
            );
        });

        // Register Facades
        $this->app->bind('auth.facade', function ($app) {
            return $app->make('auth.service');
        });

        // Register Custom JWT User Provider
        $this->app->bind(\Illuminate\Contracts\Auth\UserProvider::class, function ($app) {
            return new CustomJWTUserProvider($app->make(\App\Models\User::class));
        });

        // Register Custom JWT Auth Provider
        $this->app->singleton('custom_jwt_auth', function ($app) {
            $userProvider = $app->make(\Illuminate\Contracts\Auth\UserProvider::class);
            return new CustomJWTAuthProvider($userProvider);
        });
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        //
    }
}
