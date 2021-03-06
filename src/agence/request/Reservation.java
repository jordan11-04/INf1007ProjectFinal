package agence.request;

import agence.models.Client;
import agence.models.Vehicule;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Josue Lubaki
 * @version 1.0
 * @since 2022-04-14
 */
public class Reservation {
    private String id;
    private LocalDateTime date;
    private Client client;
    private Vehicule vehicule;
    private boolean estCours;

    // constructeur par défaut
    public Reservation() {
        this.id = UUID.randomUUID().toString();
        this.estCours = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isEstCours() {
        return estCours;
    }

    public void setEstCours(boolean estCours) {
        this.estCours = estCours;
    }

    @Override
    public String toString() {
        return "Reservation : \n" +
                "\tid = '" + id + "'\n" +
                "\tdate = " + date + "\n" +
                "\tclient=" + client + "\n" +
                "\tvehicule=" + vehicule + "\n" +
                "\testCours=" + estCours + "\n";
    }
}
