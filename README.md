# Vale Food System

Um mini sistema para gerenciamento de Restaurantes com usuários, promoções e um serviço de autenticação.
Esse projeto faz parte de um desenvolvimento para a disciplina DM11 da pós graduação em Cloud Computing da instituição INATEL.

## Criado por
#### Marcos Corazza

## Tecnologias Utilizadas
- **Java 17**
- **Spring Boot**
- **Maven**
- **Firestore**

## Firestore Database

Para utilizar o **Firestore Database** é necessário ajustar `spring.profiles.active=local` no **application.properties** e salvar as credenciais em `service-accounts.json` dentro da pasta `\src\main\resources\`

---

## Estrutura de Endpoints

---

### Autenticação
##### POST `/valefood/auth`
**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```
**Response:**
```json
{
  "token": "jwt-token"
}
```

---

### Usuários
##### POST `/valefood/users`
**Request Body:**
```json
{
    "name": "string",
    "email": "string",
    "password": "string",
    "type": "REGULAR|RESTAURANT",
    "categories": ["string"]
}
```
**Response:**
```json
{
    "name": "string",
    "email": "string",
    "type": "string",
    "categories": ["string"]
}
```

##### GET `/valefood/users`
**Response:**
```json
[
  {
      "name": "string",
      "email": "string",
      "type": "string",
      "categories": ["string"]
  }
]
```

##### GET `/valefood/users/{id}`
**Response:**
```json
{
    "name": "string",
    "email": "string",
    "type": "string",
    "categories": ["string"]
}
```

##### GET `/valefood/users/promos/{id}`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "restaurantId": "string",
    "category": "string",
    "price": float
  }
]
```

##### PUT `/valefood/users/{id}`
**Request Body:**
```json
{
    "name": "string",
    "password": "string",
    "type": "REGULAR|RESTAURANT",
    "categories": ["string"]
}
```
**Response:**
```json
{
    "name": "string",
    "email": "string",
    "type": "string",
    "categories": ["string"]
}
```

##### DELETE `/valefood/users/{id}`
**Response:** `204 No Content`

---

### Restaurantes
##### POST `/valefood/restaurants`
**Request Body:**
```json
{
  "name": "string",
  "address": "string",
  "userId": "string",
  "categories": ["string"],
  "products": [
      {
        "name": "string",
        "description": "string",
        "category": "string",
        "price"; float
      }
    ]
}
```
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "address": "string",
  "userId": "string",
  "categories": ["string"],
  "products": [
      {
        "name": "string",
        "description": "string",
        "category": "string",
        "price"; float
      }
    ]
}
```

##### GET `/valefood/restaurants`
**Response:**
```json
[
  {
    "id": "string",
    "name": "string",
    "address": "string",
    "userId": "string",
    "categories": ["string"],
    "products": [
        {
          "name": "string",
          "description": "string",
          "category": "string",
          "price"; float
        }
      ]
  }
]
```

##### GET `/valefood/restaurants/{id}`
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "address": "string",
  "userId": "string",
  "categories": ["string"],
  "products": [
      {
        "name": "string",
        "description": "string",
        "category": "string",
        "price"; float
      }
    ]
}
```

##### GET `/valefood/restaurants/promos/{id}`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "restaurantId": "string",
    "category": "string",
    "price": float
  }
]
```

##### PUT `/valefood/restaurants/{id}`
**Request Body:**
```json
{
  "name": "string",
  "address": "string",
  "userId": "string",
  "categories": ["string"],
  "products": [
      {
        "name": "string",
        "description": "string",
        "category": "string",
        "price"; float
      }
    ]
}
```
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "address": "string",
  "userId": "string",
  "categories": ["string"],
  "products": [
      {
        "name": "string",
        "description": "string",
        "category": "string",
        "price"; float
      }
    ]
}
```

##### DELETE `/valefood/restaurants/{id}`
**Response:** `204 No Content`

---

### Promoções
##### POST `/valefood/promos`
**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "restaurantId": "string",
  "category": "string",
  "price": float
}
```
**Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "restaurantId": "string",
  "category": "string",
  "price": float
}
```

##### GET `/valefood/promos`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "restaurantId": "string",
    "category": "string",
    "price": float
  }
]
```

##### GET `/valefood/promos/{id}`
**Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "restaurantId": "string",
  "category": "string",
  "price": float
}
```

##### GET `/valefood/promos/category/{category}`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "restaurantId": "string",
    "category": "string",
    "price": float
  }
]
```

##### PUT `/valefood/promos/{id}`
**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "restaurantId": "string",
  "category": "string",
  "price": float
}
```
**Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "restaurantId": "string",
  "category": "string",
  "price": float
}
```

##### DELETE `/valefood/promos/{id}`
**Response:** `204 No Content`
