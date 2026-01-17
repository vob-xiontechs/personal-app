<?php

namespace App\Providers;

use App\Repositories\Auth\UserRepository;
use App\Repositories\Auth\UserRepositoryInterface;
use App\Services\Auth\AuthService;
use App\Services\Auth\Jwt\JwtService;
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
        $this->app->singleton(JwtService::class, function ($app) {
            return new JwtService();
        });

        $this->app->singleton('auth.service', function ($app) {
            return new AuthService(
                $app->make(UserRepositoryInterface::class),
                $app->make(JwtService::class)
            );
        });

        // Register Facades
        $this->app->bind('auth.facade', function ($app) {
            return $app->make('auth.service');
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
