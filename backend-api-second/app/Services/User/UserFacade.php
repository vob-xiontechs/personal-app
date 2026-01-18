<?php

namespace App\Services\User;

use Illuminate\Support\Facades\Facade;

/**
 * @method static \App\Models\User getProfile()
 */
class UserFacade extends Facade
{
    /**
     * Get the registered name of the component.
     */
    protected static function getFacadeAccessor(): string
    {
        return 'user.service';
    }
}
