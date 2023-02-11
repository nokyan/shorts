# Shorts API

## Overview

The Shorts API provides a simple way to shorten URLs using a Rest API. It supports vanity short URLs when the client has a vanity auth token, as well as standard ID-based short URLs for everyone else.
Using an admin token, moderators can remove shortened URLs.

### Endpoints

#### POST /{vanityId}

* **Shorten URL with Vanity Token**
	+ Request Body: `longUrlString` (string)
	+ Path Variable: `vanityId` (string)
	+ Header: `VANITY_AUTH` (string)
	+ Response: `shortenedUrl` (string) on success, error code on failure

#### POST /

* **Shorten URL to Random ID**
	+ Request Body: `longUrlString` (string)
	+ Response: `shortenedUrl` (string) on success, error code on failure

#### GET /{shortUrl}

* **Redirect to Original URL**
	+ Path Variable: `shortUrl` (string)
	+ Response: HTTP redirect to original URL with status code 302

#### DELETE /{id}

* **Delete Vanity URL**
	+ Request Header: `ADMIN_AUTH` (required string)
	+ Path Variable: `id` (string)
	+ Response: null on success, error code on failure

## Authentication

* Vanity URLs require a valid authentication token (`VANITY_AUTH`) in the request header.
* Administrator access requires a valid authentication token (`ADMIN_AUTH`) in the request header.
