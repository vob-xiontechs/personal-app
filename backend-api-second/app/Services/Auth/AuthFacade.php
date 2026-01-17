<?php

namespace App\Services\Auth;

use Illuminate\Support\Facades\Facade;

/**
 * @method static array register(array $data)
 * @method static array login(array $credentials)
 * @method static bool logout()
 * @method static array refresh()
 * @method static \App\Models\User getProfile()
 */
class AuthFacade extends Facade
{
    /**
     * Get the registered name of the component.
     */
    protected static function getFacadeAccessor(): string
    {
        return 'auth.service';
    }
}
