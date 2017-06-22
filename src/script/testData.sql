insert into `Bruger` (`BrugerID`, `Navn`, `Efternavn`, `Telefon`, `Email`, `Adresse`, `Postnr`) values
(1, 'Test', 'Testsen', 0000000, 'test@testsen', 'testvej', '1337'),
(2, 'Ole', 'Testsen1', 0000000, 'test@testsen', 'testvej', '1337'),
(3, 'Test3', 'Olesen', 0000000, 'test@testsen', 'testvej', '1337'),
(4, 'Test4', 'Testsen4', 0000000, 'test@testsen', 'testvej', '1337'),
(5, 'Tes5t', 'Testsen5', 0000000, 'test@testsen', 'testvej', '1337'),
(6, 'Test', 'Testsen', 0000000, 'test@testsen', 'testvej', '1337'),
(7, 'Ole', 'Testsen1', 0000000, 'test@testsen', 'testvej', '1337'),
(8, 'Test3', 'Olesen', 0000000, 'test@testsen', 'testvej', '1337'),
(9, 'Test4', 'Testsen4', 0000000, 'test@testsen', 'testvej', '1337'),
(10, 'Tes5t', 'Testsen5', 0000000, 'test@testsen', 'testvej', '1337'),
(11, 'Test', 'Testsen', 0000000, 'test@testsen', 'testvej', '1337'),
(12, 'Ole', 'Testsen1', 0000000, 'test@testsen', 'testvej', '1337'),
(13, 'Test3', 'Olesen', 0000000, 'test@testsen', 'testvej', '1337'),
(14, 'Test4', 'Testsen4', 0000000, 'test@testsen', 'testvej', '1337'),
(15, 'Tes5t', 'Testsen5', 0000000, 'test@testsen', 'testvej', '1337'),
(16, 'Test', 'Testsen', 0000000, 'test@testsen', 'testvej', '1337'),
(17, 'Ole', 'Testsen1', 0000000, 'test@testsen', 'testvej', '1337'),
(18, 'Test3', 'Olesen', 0000000, 'test@testsen', 'testvej', '1337'),
(19, 'Test4', 'Testsen4', 0000000, 'test@testsen', 'testvej', '1337'),
(20, 'Tes5t', 'Testsen5', 0000000, 'test@testsen', 'testvej', '1337');

insert into `Tolk` (`TolkID`, `Navn`, `Efternavn`, `Telefon`, `Email`, `Adresse`, `Postnr`) values
(1, 'Tolk', 'Tolksen', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(2, 'Tolk1', 'Tolksen1', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(3, 'Tolk3', 'Tolksen3', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(4, 'Tolk4', 'Tolksen4', 0000000, 'tolk@tolksen', 'tolkvej', '1337'),
(5, 'Tol5k', 'Tolksen5', 0000000, 'tolk@tolksen', 'tolkvej', '1337');

/*insert into `Bevillinggiver` (`giverID`, `Navn`, `Telefon`, `Email`) values
(1, 'DNTM', 0000000, 'bevilling@giver.dk'),
(2, 'JC', 0000000, 'bevilling2@giver.dk'),
(3, 'KTSK', 0000000, 'bevilling3@giver.dk'),
(4, 'Egen betaling', 0000000, 'bevilling4@giver.dk'),
(5, 'Bevillinggiver5', 0000000, 'bevilling5@giver.dk');*/

insert into `Bevilling` (`Bevillingsnummer`, `Bruger`, `Navn`, `Opgave`, `Timer`) values
(1, 1, 'DNTM', 'Fodboldtræninng', 4),
(2,5,'DNTM', 'Læge',2),
(3,2,'KTSK', 'Uddannelse',2),
(4,3,'KTSK', 'Jurastudie',4),
(5,1, 'JC', 'Alt' ,1000),
(6,2,'JC', 'Møde' ,500);

insert into `Opgave` (`Opgavenummer`, `Bestiller`, `Bevillingsnummer`, `Type`, `Dato`, `StartTid`, `SlutTid`, `AntalTolk`, `Adresse`, `Postnr`, `Lokal`, `Ekstra`) values
(1,1,1,'TSP', '2017-06-15', 1600, 1800, 2, 'Skolevej 30', '2800', '301', 'Eksamen'),
(2,2,4,'TSP', '2017-06-17', 1600, 1800, 1, 'Skolevej 30', '2800', '301', 'ReEksamen'),
(3,3,3,'TSP', '2017-06-30', 1600, 1800, 1, 'Skolevej 30', '2800', '301', 'ReEksamen'),
(4,4,2,'TSP', '2017-08-15', 1600, 1800, 1, 'Skolevej 30', '2800', '301', 'Jobsamtale'),
(5,5,5,'TSP', '2017-10-15', 1600, 1800, 1, 'Kirkevej 12', '2800', 'Skibet', 'Bryllup'),
(6,5,4,'TSP', '2017-06-15', 1600, 1800, 1, 'Skolevej 1', '2000', '100', 'Forældremøde'),
(7,1,NULL, 'TSK', '2017-06-15', 1600, 1800, 1, NULL, NULL, NULL, NULL),
(8,3,NULL, NULL, '2017-06-15', 1600, NULL, 1, NULL, NULL, NULL, NULL),
(9,1,1, null, '2017-06-20', 1000, 1100, 1, null, null, null, null),
(10,1,1, null, '2017-06-20', 1100, 1200, 1, null, null, null, null),
(11,1,1, null, '2017-06-30', 1200, 1300, 1, null, null, null, null),
(12,1,1, null, '2017-06-30', 1300, 1400, 1, null, null, null, null);



insert into `Modtager` (`Opgavenummer`, `BrugerID`) values
(5, 4),
(6, 4),
(7, 2),
(8, 4);

insert into `Tilknyttet` (`Opgavenummer`, `TolkID`) values
(1, 2),
(1, 3),
(2, 1),
(3, 4),
(4, 5),
(5, 5),
(5, 4),
(9, 5),
(10, 5),
(11, 5),
(12, 5);