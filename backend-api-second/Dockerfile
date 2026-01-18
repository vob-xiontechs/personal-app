FROM php:8.2-cli

WORKDIR /var/www/html

# Install basic dependencies
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libzip-dev \
    libssl-dev \
    pkg-config \
    && docker-php-ext-install zip pdo_mysql

# Install MongoDB extension with SSL support
RUN pecl install mongodb && docker-php-ext-enable mongodb

# Install Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Copy composer files first for better caching
COPY composer.json composer.lock /var/www/html/

# Install PHP dependencies (ignore platform requirements and skip scripts)
RUN composer install --no-dev --optimize-autoloader --ignore-platform-req=php --ignore-platform-req=ext-mongodb --ignore-platform-req=ext-sodium --no-scripts

# Install Node.js
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# Copy package files
COPY package.json package-lock.json /var/www/html/

# Install Node dependencies
RUN npm install

# Copy source code
COPY . /var/www/html

# Build assets
RUN npm run build

# Copy entrypoint script
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Basic permissions
RUN chown -R www-data:www-data /var/www/html

EXPOSE 8010

# Use entrypoint script
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
