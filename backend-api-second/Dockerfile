FROM php:8.2-cli

WORKDIR /var/www/html

# Install basic dependencies
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libzip-dev \
    && docker-php-ext-install zip pdo_mysql

# Install MongoDB extension
RUN pecl install mongodb && docker-php-ext-enable mongodb

# Install Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Copy source
COPY . /var/www/html

# Basic permissions
RUN chown -R www-data:www-data /var/www/html

EXPOSE 8010

# Start Laravel development server
CMD ["php", "artisan", "serve", "--host=0.0.0.0", "--port=8010"]
