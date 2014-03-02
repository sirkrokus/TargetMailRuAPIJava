package ru.mail.target.api;

import org.junit.Test;
import ru.mail.target.api.domain.Banner;
import ru.mail.target.api.domain.BannerImage;
import ru.mail.target.api.domain.Token;
import ru.mail.target.api.jwrap.DefaultTargetAPIObjectFactory;
import ru.mail.target.api.jwrap.TokenImpl;

import javax.json.*;
import java.io.*;

/**
 * @Author Sergey Buglivykh at 19.02.14 16:39
 */
public class TargetAPITest {

    public static TargetMode targetMode =
            new TargetMode(TargetAPI.SANDBOX_URL, "Аккаунт клиента #1 - песочница",
                    new TokenImpl("xxx", "xxx"));

    @Test
    public void testJsonReadWrite() throws IOException {
        JsonReader jr = Json.createReader(new FileReader(new File("campaign-1.json")));
        JsonObject jsonObject = jr.readObject();
        System.out.println(jsonObject);

        JsonWriter jw = Json.createWriter(new FileWriter(new File("campaign-1-copy.json")));
        jw.writeObject(jsonObject);
        jw.close();
    }

    @Test
    public void testGetClientToken() throws Exception {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        Token token = targetAPI.createClientToken("364283648327@mail.ru");

        System.out.println(token);

    }

    @Test
    public void testGeoTree() throws TargetAPIException {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.getGeoTree();

        System.out.println(struct);
    }

    @Test
    public void testGetPackages() throws TargetAPIException {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.getPackages();

        System.out.println(struct);
    }

    @Test
    public void testGetCampaign() throws TargetAPIException {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.getCampaign(45676345);

        System.out.println(struct);
    }

    @Test
    public void testCreateCampaign() throws Exception {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        String json = loadFile(new File("campaign-1.json"), "utf-8");
        JsonStructure struct = Json.createReader(new StringReader(json)).read();
        System.out.println("JSON " + struct);
        JsonObject jobj = (JsonObject) struct;

        JsonObject jsonObject = targetAPI.createCampaign(jobj);

        System.out.println(jsonObject);

    }

    @Test
    public void testUpdateCampaign() throws TargetAPIException {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonObjectBuilder bld = Json.createObjectBuilder();
        bld.add("status", "active");

        JsonStructure struct = targetAPI.updateCampaign(8499384L, bld.build());

        System.out.println(struct);

    }

    @Test
    public void testGetAllCampaigns() throws TargetAPIException {

        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.getCampaigns(null);

        System.out.println(struct);

    }

    @Test
    public void testExportCampaign() throws TargetAPIException {

        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        File f = targetAPI.exportCampaign(756765756, TargetAPI.Ext.CSV, ".");

        System.out.println(f);

    }

    @Test
    public void testImportCampaign() throws TargetAPIException {

        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.importCampaign(new File("campaign-1.csv"), false);

        System.out.println(struct);
    }

    @Test
    public void testGetCampaignsBanners() throws TargetAPIException {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.getCampaignBanners(234324244, null);

        System.out.println(struct);

        JsonArray arr = (JsonArray) struct;

        for (int i = 0; i < arr.size(); i++) {
            JsonObject obj = arr.getJsonObject(i);
            int id = obj.getInt("id");
            String url = obj.getString("url");
            System.out.println(id + "-" + url);
        }

    }

    @Test
    public void testGetAllUserBanners() throws TargetAPIException {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        TargetAPIObjectFactory taof = new DefaultTargetAPIObjectFactory();

        JsonStructure struct = targetAPI.getBanners(TargetAPI.AdvertStatus.NONDELETED, null);

        //System.out.println(struct);

        JsonArray arr = (JsonArray) struct;
        System.out.println("Загружено объявлений : " + arr.size());

        for (int i = 0; i < arr.size(); i++) {
            JsonObject obj = arr.getJsonObject(i);
            Banner banner = taof.createBanner(obj);
            System.out.println(banner);
        }

    }

    @Test
    public void testUpdateCampaignsBanners() throws TargetAPIException {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);
        TargetAPIObjectFactory taof = new DefaultTargetAPIObjectFactory();

        JsonStructure struct = targetAPI.getCampaignBanners(4654637, null);

        System.out.println(struct);

        JsonArray arr = (JsonArray) struct;

        for (int i = 0; i < arr.size(); i++) {
            JsonObject obj = arr.getJsonObject(i);
            Banner banner = taof.createBanner(obj);

            System.out.println(banner);
            banner.setUrl(banner.getUrl()+"&utm_term=myterm");

            targetAPI.updateBanner(banner.getId(), Json.createObjectBuilder().add("url", banner.getUrl()).build());
        }

    }

    @Test
    public void testUploadImage() throws TargetAPIException {

        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        BannerImage img = targetAPI.uploadImage(new File("design/pict.jpg"), 90, 75);

        System.out.println(img);
    }

    @Test
    public void testGetQuickStat() throws TargetAPIException {

        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.getQuickStatistics(TargetAPI.StatisticsEntity.CAMPAIGNS, 23423434);

        System.out.println(struct);
    }

    @Test
     public void testAllGetRequestWithoutParams() throws Exception {
        TargetAPI targetAPI = new TargetAPIImpl(targetMode);

        JsonStructure struct = targetAPI.getLimits();
        System.out.println(struct);
        Thread.sleep(2000L);

        struct = targetAPI.getGeoTree();
        System.out.println(struct);
        Thread.sleep(2000L);

        struct = targetAPI.getPackages();
        System.out.println(struct);
        Thread.sleep(2000L);

        struct = targetAPI.getRemarketings();
        System.out.println(struct);
        Thread.sleep(2000L);

        struct = targetAPI.getRemarketingGroups();
        System.out.println(struct);
        Thread.sleep(2000L);

        struct = targetAPI.getRemarketingCounters();
        System.out.println(struct);
        Thread.sleep(2000L);

        struct = targetAPI.getApplications();
        System.out.println(struct);
        Thread.sleep(2000L);

        struct = targetAPI.getOdklGroupScopes();
        System.out.println(struct);
        Thread.sleep(2000L);
    }


    protected String loadFile(File file, String enc) throws Exception {
        StringBuilder buffer = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), enc));
        int data;
        while ((data = br.read()) != -1) {
            buffer.append(data);
        }
        br.close();

        return buffer.toString();
    }

}
