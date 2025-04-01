SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

CREATE TABLE `client` (
  `idClient` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `prenom` varchar(255) NOT NULL,
  `numero` int(11) NOT NULL,
  `email` text NOT NULL,
  `motDePasse` text NOT NULL,
  `adresse` text NOT NULL,
  `photo` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `commande` (
  `idCommande` int(11) NOT NULL,
  `idClient` int(11) NOT NULL,
  `date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `commanderepas` (
  `idCommandeRepas` int(11) NOT NULL,
  `idCommande` int(11) NOT NULL,
  `idRepas` int(11) NOT NULL,
  `quantite` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `repas` (
  `idRepas` int(11) NOT NULL,
  `nomRepas` varchar(255) NOT NULL,
  `prix` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `photo` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE `client`
  ADD PRIMARY KEY (`idClient`);

ALTER TABLE `commande`
  ADD PRIMARY KEY (`idCommande`),
  ADD KEY `idClient` (`idClient`);

ALTER TABLE `commanderepas`
  ADD PRIMARY KEY (`idCommandeRepas`),
  ADD KEY `idCommande` (`idCommande`),
  ADD KEY `idRepas` (`idRepas`);

ALTER TABLE `repas`
  ADD PRIMARY KEY (`idRepas`);

ALTER TABLE `client`
  MODIFY `idClient` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `commande`
  MODIFY `idCommande` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `commanderepas`
  MODIFY `idCommandeRepas` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `repas`
  MODIFY `idRepas` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `commande`
  ADD CONSTRAINT `commande_ibfk_1` FOREIGN KEY (`idClient`) REFERENCES `client` (`idClient`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `commanderepas`
  ADD CONSTRAINT `commanderepas_ibfk_1` FOREIGN KEY (`idCommande`) REFERENCES `commande` (`idCommande`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `commanderepas_ibfk_2` FOREIGN KEY (`idRepas`) REFERENCES `repas` (`idRepas`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;
