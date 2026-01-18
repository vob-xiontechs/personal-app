<?php

namespace App\Providers;

use App\Providers\CustomJWTUserProvider;
use Illuminate\Foundation\Support\Providers\AuthServiceProvider as ServiceProvider;
use Illuminate\Support\Facades\Auth;

class AuthServiceProvider extends ServiceProvider
{
    /**
     * The model to policy mappings for the application.
     *
     * @var array<class-string, class-string>
     */
    protected $policies = [
        // 'App\Models\Model' => 'App\Policies\ModelPolicy',
    ];

    /**
     * Register any authentication / authorization services.
     */
    public function boot(): void
    {
        $this->registerPolicies();

        // Register our custom JWT user provider driver
        Auth::provider('custom_jwt', function ($app, array $config) {
            return new CustomJWTUserProvider($app->make($config['model']));
        });
    }
}
