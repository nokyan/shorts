# Shorts

A simple and customizable link shortening service with vanity URL support.

## Features

* Vanity URL support with customizable tokens
* Standard ID-based short URLs
* Easy installation and usage

## Authentication

The API uses HTTP headers for authentication.

* Normal URLs that will be shortened with a random (or hash-based) ID don't need any authentication.
* Vanity URLs require a valid `VANITY_AUTH` token in the request header.
* Administrator access requires a valid `ADMIN_AUTH` token in the request header.

## Installation

To install and run the server, follow these steps:

1. Clone the repository using Git: `git clone https://github.com/nokyan/link-shortener-api.git`
2. Install dependencies using Maven
3. Configure the server by editing the `src/main/resources/application.properties` file
4. Start the server using Maven

## Usage

To use the API, send a request to one of the three endpoints with the required parameters.

* For shortening URLs, send a POST request to `/` with a URL as a raw string in the request body.
* For shortening URLs, send a POST request to `/{vanityId}` with a URL as a raw string in the request body and the VANITY_AUTH header field set to the token in `src/main/resources/application.properties`.
* For redirecting to original URLs, send a GET request to `{shortUrl}`.
* For deleting vanity URLs, send a DELETE request to `{vanityId}` with an `ADMIN_AUTH` token in the request header.

## To-do

* Docker
* More admin endpoints like blocklists for certain URLs

## License

The Link Shortener API is released under the MIT License. See the LICENSE file for details.