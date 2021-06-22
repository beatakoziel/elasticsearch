
import com.alibaba.fastjson.JSON;
import models.Player;
import models.SportClub;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();

        while (true) {
            Integer choice = printMenu();
            clearScreen();
            System.out.println(choice);
            if (choice > 0 && choice < 9) {
                switch (choice) {
                    case 1:
                        addElementToDatabase(client);
                        break;
                    case 2:
                        // editElement(playersMap, clubsMap);
                        break;
                    case 3:
                        getElementById(client);
                        break;
                    case 4:
                        getAll(client);
                        break;
                    case 5:
                        removeElement(client);
                        break;
                    case 6:
                        //calculateAveragePlayerSalary(playersMap);
                        break;
                    case 7:
                        //getElementByName(playersMap, clubsMap);
                        break;
                }
                System.out.println("Press enter to continue...");
                System.in.read();
            } else System.out.println("Wrong number, choose again.");
        }

    }

    private static void getAll(RestHighLevelClient client) throws IOException {
        System.out.println("Getting all values");
        Integer s = printSubMenu();
        SearchRequest searchRequest = new SearchRequest();
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Arrays.stream(searchHits)
                            .map(hit -> JSON.parseObject(hit.getSourceAsString(), Player.class))
                            .collect(Collectors.toList())
                            .forEach(System.out::println);
                    break;
                case 2:
                    Arrays.stream(searchHits)
                            .map(hit -> JSON.parseObject(hit.getSourceAsString(), SportClub.class))
                            .collect(Collectors.toList())
                            .forEach(System.out::println);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementById(RestHighLevelClient client) throws IOException {
        System.out.println("Getting by id");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    GetRequest getRequest = new GetRequest("players");
                    getRequest.id(playerId);

                    GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
                    System.out.println(getResponse);
                    break;
                case 2:
                    String clubId = scanner.next();
                    GetRequest getClubRequest = new GetRequest("clubs");
                    getClubRequest.id(clubId);

                    GetResponse getResponseClubs = client.get(getClubRequest, RequestOptions.DEFAULT);
                    System.out.println(getResponseClubs);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

/*    private static void getElementByName(RestHighLevelClient client) throws IOException {
        System.out.println("Getting by name");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write name:");
            switch (s) {
                case 1:
                    String playerName = scanner.next();
                    ScanQuery<UUID, Player> scan = new ScanQuery<>((IgniteBiPredicate<UUID, Player>) (uuid, c) -> c.getFirstname().equals(playerName));
                    QueryCursor<Cache.Entry<UUID, Player>> playersCollection = players.query(scan);
                    playersCollection.forEach(player -> System.out.println(player.getValue()));
                    break;
                case 2:
                    String clubName = scanner.next();
                    ScanQuery<UUID, SportClub> clubsScan = new ScanQuery<>((IgniteBiPredicate<UUID, SportClub>) (uuid, c) -> c.getName().equals(clubName));
                    QueryCursor<Cache.Entry<UUID, SportClub>> clubsCollection = clubs.query(clubsScan);
                    clubsCollection.forEach(player -> System.out.println(player.getValue()));
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }*/

/*    private static void editElement(IgniteCache<UUID, Player> players, IgniteCache<UUID, SportClub> clubs) {
        System.out.println("Editing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (isValidUUID(playerId) && players.containsKey(UUID.fromString(playerId))) {
                        Player player = getPlayerFromUser(clubs, scanner);
                        players.put(UUID.fromString(playerId), player);
                        System.out.println(playerId + " => " + player.toString());
                    } else System.out.printf("Player with id %s not found.%n", playerId);
                    break;
                case 2:
                    String clubId = scanner.next();
                    if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
                        SportClub sportClub = getSportClub(scanner);
                        clubs.put(UUID.fromString(clubId), sportClub);
                        System.out.println(clubId + " => " + sportClub.toString());
                    } else System.out.printf("Club with id %s not found.%n", clubId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }*/

    private static void removeElement(RestHighLevelClient client) {
        System.out.println("Removing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    DeleteRequest deleteRequest = new DeleteRequest("players");
                    deleteRequest.id(playerId);

                    try {
                        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
                        System.out.println(deleteResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    String clubId = scanner.next();
                    DeleteRequest deleteClubRequest = new DeleteRequest("clubs");
                    deleteClubRequest.id(clubId);

                    try {
                        DeleteResponse deleteResponse = client.delete(deleteClubRequest, RequestOptions.DEFAULT);
                        System.out.println(deleteResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

/*    private final static Pattern UUID_REGEX_PATTERN =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }*/

    private static void addElementToDatabase(RestHighLevelClient client) {
        System.out.println("Adding to database");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Player player = getPlayerFromUser(scanner);
                    XContentBuilder builder = null;
                    try {
                        builder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("firstname", player.getFirstname())
                                .field("surname", player.getSurname())
                                .field("salary", player.getSalary())
                                .endObject();
                        IndexRequest indexRequest = new IndexRequest("players");
                        indexRequest.source(builder);
                        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                        System.out.println(response.getResult());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    SportClub sportClub = getSportClub(scanner);
                    try {
                        builder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("name", sportClub.getName())
                                .endObject();
                        IndexRequest indexRequest = new IndexRequest("clubs");
                        indexRequest.source(builder);
                        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                        System.out.println(response.getResult());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

/*    private static void calculateAveragePlayerSalary(IgniteCache<UUID, Player> players) {
        System.out.println("Calculate average salary");
        List<Player> playersList = new ArrayList<>();
        for (Cache.Entry<UUID, Player> player : players) {
            playersList.add(player.getValue());
        }
        double averageSalary = playersList.stream()
                .mapToDouble(Player::getSalary)
                .average()
                .orElse(0);
        System.out.println("Average player salary: " + averageSalary);
    }*/

    private static SportClub getSportClub(Scanner scanner) {
        System.out.println("Write club name:");
        String name = scanner.next();
        System.out.println("Write creation year:");
        Integer creationYear = scanner.nextInt();
        return SportClub.builder()
                .name(name)
                .build();
    }

    private static Player getPlayerFromUser(Scanner scanner) {
        System.out.println("Write player first name:");
        String firstname = scanner.next();
        System.out.println("Write player surname:");
        String surname = scanner.next();
        System.out.println("Write player salary:");
        Integer playerSalary = scanner.nextInt();
        return Player.builder()
                .firstname(firstname)
                .surname(surname)
                .salary(playerSalary)
                .build();
    }

    private static Integer printMenu() {
        System.out.println("\nSPORTS CLUB - IGNITE");
        System.out.println("\nChoose operation:");
        System.out.println("1.ADD");
        System.out.println("2.EDIT");
        System.out.println("3.GET BY ID");
        System.out.println("4.GET ALL");
        System.out.println("5.REMOVE");
        System.out.println("6.CALCULATE AVERAGE PLAYER SALARY");
        System.out.println("7.GET BY NAME");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    private static Integer printSubMenu() {
        System.out.println("\nChoose table:");
        System.out.println("1.PLAYERS");
        System.out.println("2.SPORT CLUBS");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
