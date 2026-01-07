package com.commons.fuyoo.apptool.mysql;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MysqlOperatorExample {

    // 数据库连接配置
    private static final String URL = "****";
    private static final String USER = "****";
    private static final String PASSWORD = "****";

    // 要执行的INSERT语句
    private static final String INSERT_SQL = "INSERT INTO `a`.`a` " +
            "(`id`,`live_studio_id`,`partner_id`,`anchor_id`,`studio_type`,`studio_name`," +
            "`multi_studio_name`,`latest_live_track_id`,`chat_id`,`description`,`status`," +
            "`default_cover_image`,`anchor_cover_image`,`operator_cover_image`,`latest_cover_image`," +
            "`enable`,`show`,`parent_studio_id`,`manual_audit`,`with_good`,`register_param`," +
            "`config_ext`,`create_time`,`update_time`,`preview_cover`,`epg_chat_id`," +
            "`show_track_id`,`nickname_hidden`,`show_win_streak`) " +
            "VALUES (58135,11054189995323392,5,1170052095,2,'垫片垫片垫片',NULL," +
            "11054190022062080,875213074540949,'测试',0,'','','','',1,1,50126,0,0,'',''," +
            "'2020-11-25 17:26:28','2024-03-05 18:59:07','',NULL,0,0,0)";

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("D:\\live_studio.sql"), StandardCharsets.UTF_8);
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            for(String str : lines){
               try{
                   int rowsAffected = stmt.executeUpdate(str);
               }catch (Exception e){
                e.printStackTrace();
               }
            }

        } catch (SQLException e) {
            System.err.println("执行INSERT语句时出错:");
            e.printStackTrace();
        }
    }
}
