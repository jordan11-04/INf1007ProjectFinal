package agence.views.impl;

import agence.models.Vehicule;
import agence.request.Reservation;
import agence.storage.StockagePersistant;
import agence.views.IReservationView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Josue Lubaki
 * @version 1.0
 * @since 2022-04-20
 */

public class ReservationView implements IReservationView {

    private final StockagePersistant stockage = StockagePersistant.getInstance();

    // scanner
    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public String identificationClient() {
        System.out.println("Entrer votre Numero de Permis : ");
        return scanner.nextLine();
    }

    @Override
    public String identificationVehicule() {
        afficherListVehicules();
        System.out.println("Entrer l'immatricule du véhicule : ");
        return scanner.nextLine();
    }

    @Override
    public void afficherListVehicules() {
        System.out.println("================= Liste des vehicules =================");
        listVehicules().forEach(v -> System.out.println(v.getImmatriculation()
                + " | " + v.getModele() + " | " + v.getCouleur() + " | " + v.getPrixVehicule() + " CAD"));
    }

    @Override
    public LocalDateTime dateReservation() {
        System.out.println("Entrer la date de la reservation sous la forme : dd/MM/yyyy HH:mm");
        // format de la date : YYYY-MM-DD HH:MM
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(scanner.nextLine(), formatter);
    }

    @Override
    public void erreurReservation(Reservation reservation) {
        System.err.printf("La réservation pour le véhicule %s ne peut être effectué à la date %s.\n",
                reservation.getVehicule().getImmatriculation(), reservation.getDate().toString());
    }

    @Override
    public boolean sortir() {
        System.out.println("Voulez-vous sortir ? (Oui/Non)");
        return scanner.nextLine().trim().equalsIgnoreCase("Oui");
    }

    @Override
    public Reservation modificationReservation(Reservation reservationClient) {

        if (reservationClient == null) {
            System.err.println("Aucune réservation trouvée pour ce client.");
            return null;
        }

        System.out.println("Voici les informations de votre réservation actuelle : ");
        System.out.println(reservationClient);

        System.out.println("Voulez-vous modifier la réservation ? (Oui/Non)");
        if (scanner.nextLine().trim().equalsIgnoreCase("Non"))
            return null;

        return menuModificationReservation(reservationClient);
    }

    @Override
    public void suppressionReservation(Reservation reservationClient) {

        // si la réservation est null, on affiche un message d'erreur
        if (reservationClient == null) {
            System.err.println("La réservation n'existe pas pour ce numéro de permis.");
            return;
        }

        System.out.println("Voici les informations de la réservation : ");
        System.out.println(reservationClient);

        System.out.println("Voulez-vous supprimer la réservation ? (Oui/Non)");
        if (scanner.nextLine().trim().equalsIgnoreCase("Non"))
            return;

        stockage.deleteReservationClient(reservationClient);
        System.out.println("La réservation a été supprimée.");
    }

    @Override
    public void erreurClient() {
        System.err.println("Aucun client trouvé pour ce numéro de permis.");
    }

    @Override
    public void erreurVehicule() {
        System.err.println("Aucun véhicule trouvé pour cet immatriculation.");
    }

    /**
     * MenuView de modification de la réservation
     * @param reservation la réservation à modifier
     * */
    private Reservation menuModificationReservation(Reservation reservation) {
        boolean continuer = true;
        do {
            System.out.println("1. Modifier la date de la réservation");
            System.out.println("2. Modifier le véhicule");
            System.out.println("0. Retour");
            String choix = scanner.nextLine();
            switch (choix) {
                case "1":
                    choixDateReservation(reservation);
                    break;
                case "2":
                    choixVehicule(reservation);
                    break;
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide !\n");
            }
        } while (continuer);

        return reservation;
    }

    /**
     * MenuView de la modification de la vehicule
     * @param reservation la reservation a modifier
     * */
    private void choixVehicule(Reservation reservation) {
        System.out.println("================= Liste des vehicules =================");
        listVehicules().forEach(v -> System.out.println(v.getImmatriculation()
                + " | " + v.getModele() + " | " + v.getCouleur() + " | " + v.getPrixVehicule() + " CAD"));

        // verifier la disponibilite du vehicule
        String immatriculation;
        boolean estDisponible;
        do {
            System.out.println("Entrer l'immatriculation du nouveau véhicule : ");
            // skip nextLine()
            //scanner.nextLine();
            immatriculation = scanner.nextLine().trim();

            if (immatriculation.equals("0")) return;
            Optional<Vehicule> nouvelleVehicule = stockage.getVehiculeByImmatriculation(immatriculation);

            if(nouvelleVehicule.isEmpty()) {
                System.out.println("L'immatriculation n'existe pas");
                throw new IllegalArgumentException("L'immatriculation n'existe pas");
            }

            estDisponible = stockage.isVehiculeDisponible(nouvelleVehicule.get().getImmatriculation(), reservation.getDate());
            if (!estDisponible) {
                System.out.println("ce véhicule n'est pas disponible, veuillez en choisir un autre");
                System.out.println("Sinon Tapez 0 pour quitter");
            }
            else {
                reservation.setVehicule(nouvelleVehicule.get());
                System.out.println("Votre nouveau véhicule est : \n" + reservation.getVehicule().toString());
            }

        } while (!estDisponible);

    }

    /**
     * Methode qui permet de modifier la date de la reservation
     * @param reservation la reservation a modifier
     * */
    private void choixDateReservation(Reservation reservation) {
        String choix;
        boolean estDisponible;
        do {
            System.out.println("Entrer la nouvelle date de la réservation sous la forme : YYYY-MM-DD HH:MM");
            choix = scanner.nextLine();
            if (choix.equals("0")) return;
            LocalDateTime nouvelleDate = LocalDateTime.parse(choix);
            estDisponible = stockage.isVehiculeDisponible(reservation.getVehicule().getImmatriculation(), nouvelleDate);
            if (!estDisponible) {
                System.out.println("Le véhicule n'est pas disponible à cette date. Veuillez entrer une date avant celle-ci ("+ reservation.getDate() +")");
                System.out.println("Sinon Tapez 0 pour quitter");
            }
            else {
                reservation.setDate(nouvelleDate);
                System.out.println("Votre reservation est : \n" + reservation);
            }
        } while (!estDisponible);
    }

    private List<Vehicule> listVehicules() {
        return new ArrayList<>(stockage.getCatalogueVehicule().values());
    }
}
