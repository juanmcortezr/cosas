#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER customer_user PASSWORD 'customers';
    CREATE USER order_user PASSWORD 'orders';
    CREATE USER user_user PASSWORD 'users';

    CREATE DATABASE customers OWNER customer_user;
    GRANT ALL PRIVILEGES ON DATABASE customers TO customer_user;

    CREATE DATABASE orders OWNER order_user;
    GRANT ALL PRIVILEGES ON DATABASE orders TO order_user;

    CREATE DATABASE users OWNER user_user;
    GRANT ALL PRIVILEGES ON DATABASE users TO user_user;
    
EOSQL
