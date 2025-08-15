# Vale Food System

Um mini sistema para gerenciamento de Restaurantes com usuários, promoções e um serviço de autenticação.
Esse projeto faz parte de um desenvolvimento para a disciplina DM11 da pós graduação em Cloud Computing da instituição INATEL.

## Criado por
Marcos Corazza

## Tecnologias Utilizadas
- **Java 17**
- **Spring Boot**
- **Maven**
- **Firestore**

## Estrutura de Endpoints

### Autenticação
#### POST `/valefood/auth`
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
#### POST `/valefood/users`
**Request Body:**
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "email": "string"
}
```

#### GET `/valefood/users`
**Response:**
```json
[
  {
    "id": "string",
    "name": "string",
    "email": "string"
  }
]
```

#### GET `/valefood/users/{id}`
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "email": "string"
}
```

#### GET `/valefood/users/promos/{id}`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "category": "string",
    "restaurantId": "string"
  }
]
```

#### PUT `/valefood/users/{id}`
**Request Body:**
```json
{
  "name": "string",
  "email": "string"
}
```
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "email": "string"
}
```

#### DELETE `/valefood/users/{id}`
**Response:** `204 No Content`

---

### Restaurantes
#### POST `/valefood/restaurants`
**Request Body:**
```json
{
  "name": "string",
  "address": "string"
}
```
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "address": "string"
}
```

#### GET `/valefood/restaurants`
**Response:**
```json
[
  {
    "id": "string",
    "name": "string",
    "address": "string"
  }
]
```

#### GET `/valefood/restaurants/{id}`
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "address": "string"
}
```

#### GET `/valefood/restaurants/promos/{id}`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "category": "string",
    "restaurantId": "string"
  }
]
```

#### PUT `/valefood/restaurants/{id}`
**Request Body:**
```json
{
  "name": "string",
  "address": "string"
}
```
**Response:**
```json
{
  "id": "string",
  "name": "string",
  "address": "string"
}
```

#### DELETE `/valefood/restaurants/{id}`
**Response:** `204 No Content`

---

### Promoções
#### POST `/valefood/promos`
**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "category": "string",
  "restaurantId": "string"
}
```
**Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "category": "string",
  "restaurantId": "string"
}
```

#### GET `/valefood/promos`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "category": "string",
    "restaurantId": "string"
  }
]
```

#### GET `/valefood/promos/{id}`
**Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "category": "string",
  "restaurantId": "string"
}
```

#### GET `/valefood/promos/category/{category}`
**Response:**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "category": "string",
    "restaurantId": "string"
  }
]
```

#### PUT `/valefood/promos/{id}`
**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "category": "string"
}
```
**Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "category": "string",
  "restaurantId": "string"
}
```

#### DELETE `/valefood/promos/{id}`
**Response:** `204 No Content`
