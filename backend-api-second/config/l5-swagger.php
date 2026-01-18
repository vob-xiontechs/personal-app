<?php

return [
    'default' => 'default',

    'documentations' => [
        'default' => [
            /*
            |--------------------------------------------------------------------------
            | Swagger UI Configuration
            |--------------------------------------------------------------------------
            */

            'api' => [
        /*
        |--------------------------------------------------------------------------
        | Edit to set the api's title
        |--------------------------------------------------------------------------
        */

        'title' => 'Personal App API',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the api's version number
        |--------------------------------------------------------------------------
        */

        'version' => env('APP_VERSION', '1.0.0'),

        /*
        |--------------------------------------------------------------------------
        | Edit to set the api's description
        |--------------------------------------------------------------------------
        */

        'description' => 'API documentation for Personal App',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui base path
        |--------------------------------------------------------------------------
        */

        'basePath' => '/',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui docs path
        |--------------------------------------------------------------------------
        */

        'docsPath' => '/docs',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui docs json path
        |--------------------------------------------------------------------------
        */

        'docsJsonPath' => '/docs/swagger.json',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui oauth2 redirect path
        |--------------------------------------------------------------------------
        */

        'oauth2RedirectPath' => '/docs/oauth2-callback',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui assets path
        |--------------------------------------------------------------------------
        */

        'assetsPath' => '/docs/asset',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the api's host (optional)
        |--------------------------------------------------------------------------
        */

        'host' => env('APP_URL', 'localhost'),

        /*
        |--------------------------------------------------------------------------
        | Edit to set the api's schemes
        |--------------------------------------------------------------------------
        */

        'schemes' => ['http'],

        /*
        |--------------------------------------------------------------------------
        | Edit to set the api's consumes content type
        |--------------------------------------------------------------------------
        */

        'consumes' => ['application/json'],

        /*
        |--------------------------------------------------------------------------
        | Edit to set the api's produces content type
        |--------------------------------------------------------------------------
        */

        'produces' => ['application/json'],

        /*
        |--------------------------------------------------------------------------
        | Edit to set the api key
        |--------------------------------------------------------------------------
        */

        'apiKey' => 'apiKey',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui default model expand depth
        |--------------------------------------------------------------------------
        */

        'defaultModelExpandDepth' => 1,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui default model rendering
        |--------------------------------------------------------------------------
        */

        'defaultModelRendering' => 'model',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui default models expand depth
        |--------------------------------------------------------------------------
        */

        'defaultModelsExpandDepth' => 1,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui display request duration
        |--------------------------------------------------------------------------
        */

        'displayRequestDuration' => true,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui doc expansion
        |--------------------------------------------------------------------------
        */

        'docExpansion' => 'none',

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui filter
        |--------------------------------------------------------------------------
        */

        'filter' => true,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui max displayed tags
        |--------------------------------------------------------------------------
        */

        'maxDisplayedTags' => null,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui operations sorter
        |--------------------------------------------------------------------------
        */

        'operationsSorter' => null,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui show extensions
        |--------------------------------------------------------------------------
        */

        'showExtensions' => false,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui show common extensions
        |--------------------------------------------------------------------------
        */

        'showCommonExtensions' => false,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui tags sorter
        |--------------------------------------------------------------------------
        */

        'tagsSorter' => null,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui validator url
        |--------------------------------------------------------------------------
        */

        'validatorUrl' => null,

        /*
        |--------------------------------------------------------------------------
        | Edit to set the swagger-ui ui
        |--------------------------------------------------------------------------
        */

        'ui' => [
            'display' => [
                /*
                |--------------------------------------------------------------------------
                | Edit to set the swagger-ui default expansion
                |--------------------------------------------------------------------------
                */

                'docExpansion' => 'none',

                /*
                |--------------------------------------------------------------------------
                | Edit to set the swagger-ui filter
                |--------------------------------------------------------------------------
                */

                'filter' => true,

                /*
                |--------------------------------------------------------------------------
                | Edit to set the swagger-ui show extensions
                |--------------------------------------------------------------------------
                */

                'showExtensions' => false,

                /*
                |--------------------------------------------------------------------------
                | Edit to set the swagger-ui show common extensions
                |--------------------------------------------------------------------------
                */

                'showCommonExtensions' => false,
            ],

            'authorization' => [
                /*
                |--------------------------------------------------------------------------
                | Edit to set the swagger-ui auth persist authorization
                |--------------------------------------------------------------------------
                */

                'persistAuthorization' => true,
            ],
        ],

        /*
        |--------------------------------------------------------------------------
        | Edit to set paths to scan for swagger annotations
        |--------------------------------------------------------------------------
        */

        'paths' => [
            // Disabled - using YAML file instead
            // base_path('app'),
        ],

        /*
        |--------------------------------------------------------------------------
        | Edit to set directories to exclude from scanning
        |--------------------------------------------------------------------------
        */

        'excludes' => [
            // Disabled - using YAML file instead
            // base_path('app/Http/Controllers/Api/Auth/AuthController.php'),
        ],

        /*
        |--------------------------------------------------------------------------
        | Base path for scanning
        |--------------------------------------------------------------------------
        */

        'base' => base_path(),

        /*
        |--------------------------------------------------------------------------
        | Proxy configuration
        |--------------------------------------------------------------------------
        */

        'proxy' => false,
            ],

            'routes' => [
                /*
                 * Route for accessing api documentation interface
                 */
                'api' => 'api/documentation',

                /*
                 * Route for accessing swagger ui
                 */
                'docs' => 'docs',

                /*
                 * Route for accessing oauth2 redirect
                 */
                'oauth2_callback' => 'docs/oauth2-callback',

                /*
                 * Route group configuration
                 */
                'group_options' => [],

                /*
                 * Middleware for routes
                 */
                'middleware' => [
                    'api' => [],
                    'docs' => [],
                    'asset' => [],
                    'oauth2_callback' => [],
                ],
            ],

            'paths' => [
                /*
                 * Edit to include full URL in ui for assets
                 */
                'use_absolute_path' => env('L5_SWAGGER_USE_ABSOLUTE_PATH', true),

                /*
                * Edit to set path where swagger ui assets should be stored
                */
                'swagger_ui_assets_path' => env('L5_SWAGGER_UI_ASSETS_PATH', 'vendor/swagger-api/swagger-ui/dist/'),

                /*
                 * File name of the generated json documentation file
                 */
                'docs_json' => 'api-docs-develop.json',

                /*
                 * File name of the generated YAML documentation file
                 */
                'docs_yaml' => 'api-docs-develop.yaml',

                /*
                 * Set this to `json` or `yaml` to determine which documentation file to use in UI
                 */
                'format_to_use_for_docs' => env('L5_FORMAT_TO_USE_FOR_DOCS', 'json'),

                /*
                 * Absolute paths to directory containing the swagger annotations are stored.
                 */
                'annotations' => [
                    base_path('app'),
                ],
            ],
        ],
    ],

    /*
    |--------------------------------------------------------------------------
    | API security definitions. Will be generated into documentation file.
    |--------------------------------------------------------------------------
    */
    'security' => [
        /*
        |--------------------------------------------------------------------------
        | Examples of Security definitions
        |--------------------------------------------------------------------------
        */
        /*
        'api_key_security_example' => [ // Unique name of security
            'type' => 'apiKey', // The type of the security scheme. Valid values are "basic", "apiKey" or "oauth2".
            'description' => 'A short description for security scheme',
            'name' => 'api_key', // The name of the header or query parameter to be used.
            'in' => 'header', // The location of the API key. Valid values are "query" or "header".
        ],
        */

        /*
        'oauth2_security_example' => [ // Unique name of security
            'type' => 'oauth2', // The type of the security scheme. Valid values are "basic", "apiKey" or "oauth2".
            'description' => 'A short description for oauth2 security scheme.',
            'flow' => 'implicit', // The flow used by the OAuth2 security scheme. Valid values are "implicit", "password", "application" or "accessCode".
            'authorizationUrl' => 'http://example.com/auth', // The authorization URL to be used for (implicit/accessCode flow).
            'tokenUrl' => 'http://example.com/auth', // The token URL to be used for (password/application/accessCode flow).
            'refreshUrl' => 'http://example.com/auth', // The URL to be used for obtaining refresh tokens (password/application/accessCode flow).
            'scopes' => [
                'read' => 'allows reading resources',
                'write' => 'allows writing resources',
            ]
        ],
        */

        /*
        |--------------------------------------------------------------------------
        | JWT Bearer Token Security
        |--------------------------------------------------------------------------
        */

        'bearerAuth' => [
            'type' => 'apiKey',
            'description' => 'JWT Authorization header using the Bearer scheme.',
            'name' => 'Authorization',
            'in' => 'header',
        ],
    ],

    /*
    |--------------------------------------------------------------------------
    | Swagger UI configuration
    |--------------------------------------------------------------------------
    */

    'defaults' => [
        'ui' => [
            'display' => [
                'dark_mode' => false,
                'docExpansion' => 'none',
                'filter' => true,
                'showExtensions' => false,
                'showCommonExtensions' => false,
            ],
            'authorization' => [
                'persistAuthorization' => true,
                'persist_authorization' => true,
                'oauth2' => [
                    'use_pkce_with_authorization_code_grant' => false,
                ],
            ],
        ],
        'securityDefinitions' => [
            'securitySchemes' => [],
        ],
    ],

    /*
    |--------------------------------------------------------------------------
    | Generate command configuration
    |--------------------------------------------------------------------------
    */

    'generate_always' => env('L5_SWAGGER_GENERATE_ALWAYS', false),

    'generate_yaml_copy' => env('L5_SWAGGER_GENERATE_YAML_COPY', true),

    'proxy' => false,

    'additional_config_url' => null,

    'operations_sort' => env('L5_SWAGGER_OPERATIONS_SORT', null),

    'validator_url' => null,

    /*
    |--------------------------------------------------------------------------
    | Turn this off to remove swagger generation on production
    |--------------------------------------------------------------------------
    */

    'generate_docs' => env('L5_SWAGGER_GENERATE_DOCS', true),

    /*
    |--------------------------------------------------------------------------
    | Edit to trust the proxy's ip address - needed for AWS Load Balancer
    |--------------------------------------------------------------------------
    */

    'proxy' => false,

    /*
    |--------------------------------------------------------------------------
    | Configs plugin allows to fetch external configs instead of passing them to SwaggerUIBundle.
    | See more at: https://github.com/swagger-api/swagger-ui#configs-plugin
    |--------------------------------------------------------------------------
    */

    'additional_config_url' => null,

    /*
    |--------------------------------------------------------------------------
    | Apply a sort to the operation list of each API. Important for grouping.
    | Set to null for manual sorting with @OA\Tag or a custom sort
    |--------------------------------------------------------------------------
    */

    'operations_sort' => env('L5_SWAGGER_OPERATIONS_SORT', null),

    /*
    |--------------------------------------------------------------------------
    | Uncomment to pass the swagger validator url
    |--------------------------------------------------------------------------
    */

    'validator_url' => null,

    /*
    |--------------------------------------------------------------------------
    | Publisher Configuration
    |--------------------------------------------------------------------------
    */

    'paths' => [
        /*
        |--------------------------------------------------------------------------
        | Absolute path to a directory where documentation files are stored
        |--------------------------------------------------------------------------
        */

        'docs' => storage_path('api-docs'),

        /*
        |--------------------------------------------------------------------------
        | Absolute path to a directory where swagger-ui assets should be published
        |--------------------------------------------------------------------------
        */

        /*
        |--------------------------------------------------------------------------
        | File name of the generated json documentation file
        |--------------------------------------------------------------------------
        */

        'docs_json' => 'api-docs-develop.json',

        /*
        |--------------------------------------------------------------------------
        | File name of the generated YAML documentation file
        |--------------------------------------------------------------------------
        */

        'docs_yaml' => 'api-docs-develop.yaml',

        /*
        |--------------------------------------------------------------------------
        | Absolute paths to directories where swagger-ui views will be published
        |--------------------------------------------------------------------------
        */

        'views' => resource_path('views/vendor/l5-swagger'),

        /*
        |--------------------------------------------------------------------------
        | Absolute path to directory where swagger-ui assets will be published
        |--------------------------------------------------------------------------
        */

        'assets' => public_path('vendor/l5-swagger'),

        /*
        |--------------------------------------------------------------------------
        | Absolute path to a directory where swagger annotations will be generated
        |--------------------------------------------------------------------------
        */

        'annotations' => storage_path('api-docs'),

        /*
        |--------------------------------------------------------------------------
        | Absolute path to a directory where swagger docs will be generated
        |--------------------------------------------------------------------------
        */

        'docs_assets' => public_path('docs'),

        /*
        |--------------------------------------------------------------------------
        | Driver for Http requests
        |--------------------------------------------------------------------------
        */

        'format_to_use_for_docs' => env('L5_FORMAT_TO_USE_FOR_DOCS', 'json'),

        /*
        |--------------------------------------------------------------------------
        | Absolute path to a directory where swagger docs will be generated
        |--------------------------------------------------------------------------
        */

        'cache' => storage_path('api-docs'),

        /*
        |--------------------------------------------------------------------------
        | Selected Swagger version
        |--------------------------------------------------------------------------
        */

        'swagger_version' => env('SWAGGER_VERSION', '2.0'),
    ],

    /*
    |--------------------------------------------------------------------------
    | Generator URLs
    |--------------------------------------------------------------------------
    */

    'constants' => [
        /*
        |--------------------------------------------------------------------------
        | Constants which can be used in annotations
        |--------------------------------------------------------------------------
        */
        'L5_SWAGGER_CONST_HOST' => env('L5_SWAGGER_CONST_HOST', 'http://localhost'),
    ],
];
