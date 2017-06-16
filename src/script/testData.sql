insert into `Bruger` (`BrugerID`, `Navn`, `Efternavn`, `Telefon`, `Email`, `Adresse`, `Postnr`) values
(1, 'Test', 'Testsen', 0000000, 'test@testsen', 'testvej', '1337'),
(2, 'Test1', 'Testsen1', 0000000, 'test@testsen', 'testvej', '1337'),
(3, 'Test3', 'Testsen3', 0000000, 'test@testsen', 'testvej', '1337'),
(4, 'Test4', 'Testsen4', 0000000, 'test@testsen', 'testvej', '1337'),
(5, 'Tes5t', 'Testsen5', 0000000, 'test@testsen', 'testvej', '1337');

insert into `Tolk` (`TolkID`, `Navn`, `Efternavn`, `Telefon`, `Email`, `Adresse`, `Postnr`) values
(1, 'Tolk', 'Tolksen', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(2, 'Tolk1', 'Tolksen1', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(3, 'Tolk3', 'Tolksen3', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(4, 'Tolk4', 'Tolksen4', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(5, 'Tol5k', 'Tolksen5', 0000000, 'tolk@tolksen', 'tolkvej', '1337');

insert into `Bevillinggiver` (`giverID`, `Navn`, `Telefon`, `Email`) values
(1, 'Bevillinggiver', 0000000, 'bevilling@giver.dk'),
(2, 'Bevillinggiver2', 0000000, 'bevilling2@giver.dk'),
(3, 'Bevillinggiver3', 0000000, 'bevilling3@giver.dk'),
(4, 'Bevillinggiver4', 0000000, 'bevilling4@giver.dk'),
(5, 'Bevillinggiver5', 0000000, 'bevilling5@giver.dk');

insert into `Bevilling` (`Bevillingsnummer`, `Bruger`, `Giver`, `Timer`, `BrugtTimer`) values
(1, 1, 1, 4, 2),
(2,5,3,2,0),
(3,2,1,2,0),
(4,3,2,4,1),
(5,1,5,1000,3),
(6,2,4,500,500);

insert into `Opgave` (`Opgavenummer`, `Bevillingsnummer`, `Type`, `StartDatoTid`, `Længde`, `Adresse`, `Postnr`, `Lokal`, `Ekstra`) values
(1,1,'TSP', '2017-06-15 16:00:00', '120', 'Skolevej 30', '2800', '301', 'Eksamen'),
(2,4,'TSP', '2017-06-17 12:00:00', '120', 'Skolevej 30', '2800', '301', 'ReEksamen'),
(3,3,'TSP', '2017-06-30 23:00:00', '120', 'Skolevej 30', '2800', '301', 'ReEksamen'),
(4,2,'TSP', '2017-08-15 2:00:00', '120', 'Skolevej 30', '2800', '301', 'Jobsamtale'),
(5,5,'TSP', '2017-10-15 10:00:00', '120', 'Kirkevej 12', '2800', 'Skibet', 'Bryllup'),
(6,4,'TSP', '2017-06-15 16:00:00', '60', 'Skolevej 1', '2000', '100', 'Forældremøde'),
(7,NULL, 'TSK', '2017-06-15 16:00:00', '60', NULL, NULL, NULL, NULL),
(8, NULL, NULL, '2017-06-15 10:00:00', NULL, NULL, NULL, NULL, NULL);

insert into `Modtager` (`Opgavenummer`, `BrugerID`) values
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(5, 4),
(6, 5),
(6, 4),
(7, 1),
(8, 3);

insert into `Tilknyttet` (`Opgavenummer`, `TolkID`) values
(1, 2),
(1, 3),
(2, 1),
(3, 4),
(4, 5),
(5, 5),
(5, 4),
(6, 1);