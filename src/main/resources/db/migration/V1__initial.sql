
SET search_path TO public;


-- 1. Postadresse
CREATE TABLE IF NOT EXISTS  PostAddress (
    Id BIGSERIAL PRIMARY KEY,
    PostCode VARCHAR(255) NOT NULL,
    City VARCHAR(255) NOT NULL,
    StreetName VARCHAR(255) NOT NULL,
    HouseNumber VARCHAR(255) NOT NULL,
    Country VARCHAR(255) NOT NULL
);

-- 2. Brukere
CREATE TABLE IF NOT EXISTS  Users (
    UserId BIGSERIAL PRIMARY KEY,
    FirstName VARCHAR(255) NOT NULL,
    LastName VARCHAR(255) NOT NULL,
    Telephone VARCHAR(20) NOT NULL,
    MailAddress VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Role VARCHAR(50) NOT NULL,
    Address_Id BIGINT NOT NULL,
    FOREIGN KEY (Address_Id) REFERENCES PostAddress(Id)
);

-- 3. Lokasjoner
CREATE TABLE IF NOT EXISTS  Locations (
    Id BIGSERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description TEXT NOT NULL,
    Address_Id BIGINT NOT NULL,
    FOREIGN KEY (Address_Id) REFERENCES PostAddress(Id)
);

-- 4. Arrangør
CREATE TABLE IF NOT EXISTS  Organizer (
    Id BIGSERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Email VARCHAR(255),
    Phone VARCHAR(50)
);

-- 5. Aktiviteter
CREATE TABLE IF NOT EXISTS  Activity (
    ActivityId BIGSERIAL PRIMARY KEY,
    ActivityType VARCHAR(255) NOT NULL,
    HoldPlace VARCHAR(255) NOT NULL,
    Description TEXT NOT NULL,
    StartTime TIMESTAMP NOT NULL,
    EndTime TIMESTAMP NOT NULL,
    Location_Id BIGINT NOT NULL,
    Organizer_Id BIGINT NOT NULL,
    FOREIGN KEY (Location_Id) REFERENCES Locations(Id),
    FOREIGN KEY (Organizer_Id) REFERENCES Organizer(Id)
);

-- 6. Deltakere
CREATE TABLE IF NOT EXISTS Participants (
    Id BIGSERIAL PRIMARY KEY,
    UserId BIGINT NOT NULL,
    Activity_Id BIGINT NOT NULL,
    Registered_At TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES Users(UserId),
    FOREIGN KEY (Activity_Id) REFERENCES Activity(ActivityId)
);


--ALTER TABLE users ADD COLUMN password VARCHAR(255);
