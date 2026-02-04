
B
Bakong Gateway
Pricing
Dashboard
Get API Key
Resources

    Postman Collection
    API Playground

Documentation Hero
API Reference

Build world-class payment experiences with Bakong Gateway.

The Bakong Gateway API is organized around REST. Our API has predictable resource-oriented URLs, accepts form-encoded request bodies, returns JSON-encoded responses, and uses standard HTTP response codes, authentication, and verbs.
API Endpoint
https://horrorbringer.site/api/v1
HTTPS Only

All API requests must be made over HTTPS. Calls made over plain HTTP will fail. API requests without authentication will also fail.
Authentication

Authenticate your requests by including your API key in the X-API-Key header.
X-API-Key: bkg_live_xxxxxxxxxxxxxxxxxxxx

Keep your API key secret. Do not share it in client-side code, public repositories, or with unauthorized individuals. Use restricted keys for production.
KHQR API

Generate and manage KHQR codes for payments.
POST
/bakong/khqr/generate

Creates a standard KHQR string for the specified merchant and amount.

Important: Verification Limitation

Payment verification via /payment/check ONLY supports Dynamic KHQR codes (links created with a specific amount).

If you omit the amount, a Static QR is generated which cannot be verified by this API. Use webhooks for static QR verification.
Field	Type	Req	Description
bakongAccountId	String	Yes	Target Bakong account ID (e.g., recipient@acleda)
merchantName	String	Yes	Maximum 25 characters
amount	Number	No	Required for verification support.
currency	Enum	No	USD or KHR (default: USD)
Request Example

curl -X POST https://horrorbringer.site/api/v1/bakong/khqr/generate \
  -H "X-API-Key: bkg_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "bakongAccountId": "your_bakong_id@bank",
    "merchantName": "My Store",
    "amount": 10.00,
    "currency": "USD",
    "billNumber": "INV-001"
  }'

Response Example

{
  "success": true,
  "data": {
    "qr": "00020101021230550013merchant@bank0110My Store...",
    "md5": "4af76d5fd2922...",
    "type": "dynamic",
    "expiresAt": "2026-01-14T03:03:08.438Z"
  }
}

POST
/bakong/khqr/generate-image

Generates a KHQR code along with a visual QR code image.
Parameters

    format
    : 'png' or 'svg'
    width
    : Image width in pixels

Request Example

curl -X POST https://horrorbringer.site/api/v1/bakong/khqr/generate-image \
  -H "X-API-Key: bkg_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "bakongAccountId": "your_bakong_id@bank",
    "merchantName": "My Store",
    "amount": 10.00,
    "format": "png",
    "width": 400
  }'

Response Example

{
  "success": true,
  "data": {
    "qr": "000201...",
    "md5": "4af76d5fd2922...",
    "type": "dynamic",
    "expiresAt": "2026-01-14T03:03:08.438Z",
    "qrImage": "data:image/png;base64,...",
    "format": "dataUrl",
    "width": 300
  }
}

Payment API

Verify transaction status and handle payments.
POST
/bakong/payment/check

Checks the status of a specific payment using its MD5 hash. Returns immediately with the current state.

Use this endpoint to poll the status of a transaction. For the best user experience, we recommend polling every 3-5 seconds.
Supported Errors

    Static QR Codes (Error Code 2)
    Transaction Failed (Error Code 1)

Request Example

curl -X POST https://horrorbringer.site/api/v1/bakong/payment/check \
  -H "X-API-Key: bkg_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "md5": "abc123def456..."
  }'

Response Example

{
  "success": true,
  "data": {
    "status": "COMPLETED",
    "payment": {
      "hash": "4af76d5fd2922...",
      "fromAccountId": "sender@bank",
      "toAccountId": "merchant@bank",
      "amount": 10.00,
      "currency": "USD",
      "transactionId": "TXN_123456789",
      "time": "2026-01-14T03:03:08.438Z"
    }
  }
}

Payment Status Definitions
Status	Description
PENDING	The payment has not been detected yet. Continue polling or wait for user to scan the QR.
COMPLETED	Payment successful. The payment object will be present in the response.
TIMEOUT	Only returned by check-with-poll if no payment is found before the window expires.
POST
/bakong/payment/check-with-poll

Waits (polls) for a payment to be completed. Returns when payment is detected or timeout occurs.
Webhooks

Get notified instantly when events happen in your account.
Security & Verification

Bakong Gateway signs the webhook events it sends to your endpoints by including a signature in each event's X-Webhook-Signature header. This allows you to verify that the events were sent by us and not by a third party.
Supported Event Types
payment.received

Sent when a payment is successful
payment.expired

Sent when dynamic QR expires
Interactive Webhook Payload

{
  "id": "evt_123456789",
  "type": "payment.received",
  "created": "2026-01-14T02:30:00Z",
  "data": {
    "md5": "4af76d5fd2922...",
    "amount": 10.00,
    "currency": "USD",
    "externalTransactionId": "tx_987654321"
  }
}

Rate Limits

To ensure platform stability, we enforce rate limits based on your subscription. We include rate limit information in every response header:
Response Headers

    X-RateLimit-Limit
    Total requests allowed per minute
    X-RateLimit-Remaining
    Remaining requests in the current window
    X-RateLimit-Reset
    Unix timestamp when the limit resets

80%Remaining

Real-time tracking of your API usage.
Error Handling

Bakong Gateway uses standard HTTP response codes to indicate the success or failure of an API request.
Technical & Syntax Errors
HTTP	Code	Description
400	BAD_REQUEST	Missing required params or malformed JSON body.
422	VALIDATION_ERROR	Input failed validation (e.g., amount too large).
Business Logic Errors
IMPORTANT
HTTP	Code	Business Context
403	QUOTA_EXCEEDED	Your subscription plan's monthly request limit has been reached.
429	RATE_LIMIT_EXCEEDED	Too many requests per minute. Upgrade plan to increase bursting capability.
403	FORBIDDEN	Key has been revoked, expired, or IP address is not whitelisted.
Upstream Connection
502	BAKONG_API_ERROR	Internal communication failure with Bakong central system.
SDK & Code Examples
Node.js
npm install axios

const axios = require('axios');
const API_KEY = 'bkg_your_api_key_here';
const BASE_URL = 'https://horrorbringer.site/api/v1';

async function generateKHQR(data) {
  const response = await axios.post(`${BASE_URL}/bakong/khqr/generate`, data, {
    headers: { 'X-API-Key': API_KEY }
  });
  return response.data;
}

Go

package main

import (
    "bytes"
    "encoding/json"
    "net/http"
)

func main() {
    url := "https://horrorbringer.site/api/v1/bakong/khqr/generate"
    payload, _ := json.Marshal(map[string]interface{}{
        "bakongAccountId": "merchant@bank",
        "merchantName":    "My Store",
        "amount":          10.0,
    })

    req, _ := http.NewRequest("POST", url, bytes.NewBuffer(payload))
    req.Header.Set("X-API-Key", "bkg_your_api_key_here")
    req.Header.Set("Content-Type", "application/json")

    client := &http.Client{}
    resp, _ := client.Do(req)
    defer resp.Body.Close()
}

PHP

<?php
$apiKey = 'bkg_your_api_key_here';
$data = [
    'bakongAccountId' => 'merchant@bank',
    'merchantName' => 'My Store',
    'amount' => 10.00
];

$ch = curl_init('https://horrorbringer.site/api/v1/bakong/khqr/generate');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json.encode($data));
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'X-API-Key: ' . $apiKey,
    'Content-Type: application/json'
]);

$response = curl_exec($ch);
curl_close($ch);
?>

B

© 2026 Bakong Gateway. Built for developers.
