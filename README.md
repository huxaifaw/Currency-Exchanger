**ExchangeRate-API Integration**

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
--header 'Authorization: ••••••' \
--header 'Cookie: JSESSIONID=3F2D4D3F6AA0DAD436FF6EF97F3E344C' \
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
- 39701.04382324219
---
**Assumptions**
- One point in implementation states that: **For every $100 on the bill, there is a $5 discount.**. But as we are implementing a currency exchange service, using the dollar sign here is somewhat irrelevant.
- I have taken that assumption that 100 and 5 numbers are the amount here irrespective of the currency.
---
**UML Diagram**

- https://github.com/huxaifaw/Currency-Exchanger/blob/main/UML-Diagram.png
---
**Authentication and Authorization**

- I have implemented basic auth to the /api/calculate endpoint
- username: huzaifa.waseem
- password: temp123
---
**Running the Service (Bonus)**
- Build the project: mvn clean install
- For getting java code coverage report: mvn verify jacoco:report
- For getting sonarqube report: mvn sonar:sonar
