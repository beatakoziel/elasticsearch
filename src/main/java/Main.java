
import com.alibaba.fastjson.JSON;
import models.Employee;
import models.Movie;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
                        editElement(client);
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
                        //calculateAverageemployeesalary(employeesMap);
                        break;
                    case 7:
                        getElementByName(client);
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
                            .map(hit -> {
                                Employee p = JSON.parseObject(hit.getSourceAsString(), Employee.class);
                                p.setId(hit.getId());
                                return p;
                            })
                            .collect(Collectors.toList())
                            .forEach(System.out::println);
                    break;
                case 2:
                    Arrays.stream(searchHits)
                            .map(hit -> {
                                Movie p = JSON.parseObject(hit.getSourceAsString(), Movie.class);
                                p.setId(hit.getId());
                                return p;
                            })
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
                    GetRequest getRequest = new GetRequest("employees");
                    getRequest.id(playerId);

                    GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
                    System.out.println(getResponse);
                    break;
                case 2:
                    String clubId = scanner.next();
                    GetRequest getClubRequest = new GetRequest("movies");
                    getClubRequest.id(clubId);

                    GetResponse getResponseClubs = client.get(getClubRequest, RequestOptions.DEFAULT);
                    System.out.println(getResponseClubs);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementByName(RestHighLevelClient client) throws IOException {
        System.out.println("Getting by name");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write name:");
        String name = scanner.next();
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(QueryBuilders.matchQuery("firstname", name));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Arrays.stream(searchHits)
                            .map(hit -> {
                                Employee p = JSON.parseObject(hit.getSourceAsString(), Employee.class);
                                p.setId(hit.getId());
                                return p;
                            })
                            .collect(Collectors.toList())
                            .forEach(System.out::println);
                    break;
                case 2:
                    Arrays.stream(searchHits)
                            .map(hit -> {
                                Movie p = JSON.parseObject(hit.getSourceAsString(), Movie.class);
                                p.setId(hit.getId());
                                return p;
                            })
                            .collect(Collectors.toList())
                            .forEach(System.out::println);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void editElement(RestHighLevelClient client) {
        System.out.println("Editing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write id:");
        String id = scanner.next();
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Employee employee = getPlayerFromUser(scanner);
                    XContentBuilder builder = null;
                    try {
                        builder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("firstname", employee.getFirstname())
                                .field("surname", employee.getSurname())
                                .field("salary", employee.getSalary())
                                .endObject();
                        UpdateRequest indexRequest = new UpdateRequest("employees", id);
                        indexRequest.doc(builder);
                        UpdateResponse response = client.update(indexRequest, RequestOptions.DEFAULT);
                        System.out.println(response.getResult());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Movie movie = getSportClub(scanner);
                    try {
                        builder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("name", movie.getName())
                                .endObject();
                        UpdateRequest indexRequest = new UpdateRequest("movies", id);
                        indexRequest.doc(builder);
                        UpdateResponse response = client.update(indexRequest, RequestOptions.DEFAULT);
                        System.out.println(response.getResult());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void removeElement(RestHighLevelClient client) {
        System.out.println("Removing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    DeleteRequest deleteRequest = new DeleteRequest("employees");
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
                    DeleteRequest deleteClubRequest = new DeleteRequest("movies");
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
                    Employee employee = getPlayerFromUser(scanner);
                    XContentBuilder builder = null;
                    try {
                        builder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("firstname", employee.getFirstname())
                                .field("surname", employee.getSurname())
                                .field("salary", employee.getSalary())
                                .endObject();
                        IndexRequest indexRequest = new IndexRequest("employees");
                        indexRequest.source(builder);
                        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                        System.out.println(response.getResult());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Movie movie = getSportClub(scanner);
                    try {
                        builder = XContentFactory.jsonBuilder()
                                .startObject()
                                .field("name", movie.getName())
                                .endObject();
                        IndexRequest indexRequest = new IndexRequest("movies");
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

/*    private static void calculateAverageemployeesalary(IgniteCache<UUID, Player> employees) {
        System.out.println("Calculate average salary");
        List<Player> employeesList = new ArrayList<>();
        for (Cache.Entry<UUID, Player> player : employees) {
            employeesList.add(player.getValue());
        }
        double averageSalary = employeesList.stream()
                .mapToDouble(Player::getSalary)
                .average()
                .orElse(0);
        System.out.println("Average player salary: " + averageSalary);
    }*/

    private static Movie getSportClub(Scanner scanner) {
        System.out.println("Write club name:");
        String name = scanner.next();
        System.out.println("Write creation year:");
        Integer creationYear = scanner.nextInt();
        return Movie.builder()
                .name(name)
                .build();
    }

    private static Employee getPlayerFromUser(Scanner scanner) {
        System.out.println("Write employee first name:");
        String firstname = scanner.next();
        System.out.println("Write employee surname:");
        String surname = scanner.next();
        System.out.println("Write employee salary:");
        Integer employeesalary = scanner.nextInt();
        return Employee.builder()
                .firstname(firstname)
                .surname(surname)
                .salary(employeesalary)
                .build();
    }

    private static Integer printMenu() {
        System.out.println("\nCINEMA - ELASTICSEARCH");
        System.out.println("\nChoose operation:");
        System.out.println("1.ADD");
        System.out.println("2.EDIT");
        System.out.println("3.GET BY ID");
        System.out.println("4.GET ALL");
        System.out.println("5.REMOVE");
        System.out.println("6.CALCULATE AVERAGE EMPLOYEE SALARY");
        System.out.println("7.GET BY NAME");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    private static Integer printSubMenu() {
        System.out.println("\nChoose table:");
        System.out.println("1.EMPLOYEES");
        System.out.println("2.MOVIES");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
