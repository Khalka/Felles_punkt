-- Sett inn postadresser
INSERT INTO PostAddress (PostCode, City, StreetName, HouseNumber, Country) VALUES
('0001', 'Oslo', 'Storgata', '1', 'Norway'),
('5003', 'Bergen', 'Bryggen', '23B', 'Norway'),
('7013', 'Trondheim', 'Elgeseter gate', '10', 'Norway');

-- Sett inn brukere
-- Sett inn brukere med roller og passord (passord = 'password' for alle, BCrypt-hash)
INSERT INTO Users (FirstName, LastName, Telephone, MailAddress, Password, Role, Address_Id) VALUES
('Kari', 'Nordmann', 12345678, 'kari@example.com',
 '$2a$10$7sW9XvZx1QaRm1f0BtvWru2cK/U6VK.Dt7bPx74BLDK0E8A3V8C5S', 'USER', 1),
('Ola', 'Nordmann', 87654321, 'ola@example.com',
 '$2a$10$7sW9XvZx1QaRm1f0BtvWru2cK/U6VK.Dt7bPx74BLDK0E8A3V8C5S', 'USER', 2),
('Eva', 'Hansen', 11223344, 'eva@example.com',
 '$2a$10$7sW9XvZx1QaRm1f0BtvWru2cK/U6VK.Dt7bPx74BLDK0E8A3V8C5S', 'ADMIN', 3);

-- Sett inn lokasjoner
INSERT INTO Locations (Name, Description, Address_Id) VALUES
('Oslo Konferansesenter', 'Stort konferansesenter i Oslo sentrum', 1),
('Bergen Kulturhus', 'Kulturhus med mange fasiliteter', 2);

-- Sett inn arrangører
INSERT INTO Organizer (Name, Email, Phone) VALUES
('Frivillighet Norge', 'kontakt@frivillighet.no', '22334455'),
('Ung Aktiv', 'info@ungaktiv.no', '99887766');

-- Sett inn aktiviteter
INSERT INTO Activity (ActivityType, HoldPlace, Description, StartTime, EndTime, Location_Id, Organizer_Id) VALUES
('Workshop', 'Oslo Konferansesenter', 'Læringsworkshop om bærekraft', '2025-07-01 10:00:00', '2025-07-01 15:00:00', 1, 1),
('Konsert', 'Bergen Kulturhus', 'Sommerkonsert med lokale band', '2025-08-15 18:00:00', '2025-08-15 22:00:00', 2, 2);

-- Sett inn deltakere
INSERT INTO Participants (UserId, Activity_Id, Registered_At) VALUES
(1, 1, CURRENT_TIMESTAMP),
(2, 1, CURRENT_TIMESTAMP),
(3, 2, CURRENT_TIMESTAMP);
