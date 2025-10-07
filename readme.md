for å fikse tailwind: npm install -D tailwindcss@3 postcss autoprefixer
npx tailwindcss init -p
Installert vue
databasen psql -U postgres -d felleskaps_punkt
password heihei123
user : myuser pass: heihei123



### Kjøre Prosjekt

I back-end:

`mvn spring-boot:run`

Front-end:

`cd frontend npm run dev `

## Viktige kommandoer Underveis i prosjektet

- psql -U postgres -d felleskaps_punkt
- mvn clean package
- mvn flyway:repair
- mvn flyway:migrate
- npm audit -> npm audit fix
- Slette tabeller og sekvenser i postgres ->

  ```
  DROP SCHEMA public CASCADE; 
  CREATE SCHEMA public;