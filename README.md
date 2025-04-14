- I have used Pair **ExchangeRate-API** endpoint in my code for getting the conversion rate from base currency to target currency.
- API documentation url: https://www.exchangerate-api.com/docs/pair-conversion-requests
---
- The **currency-exchange-service** has one endpoint that returns the net payable amount in the specified target currency after applying applicable discounts and currency conversion.

**API details are as follows:**
- Request Method: POST
- Request URL: http://localhost:8080/api/calculate
- Request Body:
  {
    "items": [
      {
        "name": "string",
        "category": "grocery | non_grocery",
        "price": number
      }
    ],
    "totalAmount": number,
    "userType": "customer | employee | affiliate",
    "customerTenure": number,
    "originalCurrency": "string",
    "targetCurrency": "string"
  }
---
**Sample Request:**

curl --location 'http://localhost:8080/api/calculate' \
--header 'Content-Type: application/json' \
--data '{
"items": [
    {
        "name": "item1",
        "category": "grocery",
        "price": 20.5
    },
    {
        "name": "item2",
        "category": "non_grocery",
        "price": 57.2
    },
    {
        "name": "item3",
        "category": "non_grocery",
        "price": 68.9
    }
    ],
    "totalAmount": 146.6,
    "userType": "customer",
    "customerTenure": 1,
    "originalCurrency": "USD",
    "targetCurrency": "PKR"
}'

**Sample Response:**
39701.04382324219
---
**Assumptions**
- One point in implementation states that: **For every $100 on the bill, there is a $5 discount.**. But as we are implementing a currency exchange service, using the dollar sign here is somewhat irrelevant.
- I have taken that assumption that 100 and 5 numbers are the amount here irrespective of the currency.
---
**Running the Service**

