# -*- coding: utf-8 -*-
import pymysql
import pandas as pd
import sys
import shutil

position_dist = {1: 'top_bar',
                 21: 'top_left',
                 22: 'top_center',
                 23: 'top_right',
                 3: 'upper_third',
                 4: 'middle_center',
                 5: 'lower_third',
                 61: 'bottom_left',
                 62: 'bottom_center',
                 63: 'bottom_right',
                 7: 'bottom_bar'}

boolean_dist = {1: 'true',
                0: 'false'}


class DB():

    def __init__(self, host='35.229.17.244', user='joezie', passwd='yigemima',
                db='smartmirrorschema', charset='utf8'):
        # 建立连接
        self.conn = pymysql.connect(host=host, user=user, passwd=passwd,
                                   db=db, charset=charset)
        #self.name = user_name

    def __enter__(self):
        return self

    def read_user_table(self, user_name=''):
        self.user_name = user_name
        sql_read_user_table = "select * from user_module_table where username = \'" + self.user_name + "\'"
        self.user_info = pd.read_sql(sql_read_user_table,self.conn)

        return self.user_info
    def read_stock_table(self):
        sql_read_stock_table = "select * from stock_module_config_table where username = \'" + self.user_name + "\'"
        self.stock_info = pd.read_sql(sql_read_stock_table,self.conn)
        return self.stock_info
    def read_youtube_table(self):
        sql_read_youtube_table = "select * from youtube_module_config_table where username = \'" + self.user_name + "\'"
        self.youtube_info = pd.read_sql(sql_read_youtube_table,self.conn)
        return self.youtube_info
    def read_covid_table(self):
        sql_read_covid_table = "select * from covid_module_config_table where username = \'" + self.user_name + "\'"
        self.covid_info = pd.read_sql(sql_read_covid_table,self.conn)
        return self.covid_info
    def __exit__(self, exc_type, exc_val, exc_tb):
        # 关闭数据库连接
        self.conn.close()

def main(user_name):
    with DB() as db:
        db.read_user_table(user_name=user_name)
        #db.print()
        #print(db.user_info)
        filename = 'config_'+user_name+'.js'
        shutil.copy('myconfig.js.sample',filename)
        file = open(filename, "a")


        # Stock Module
        if(db.user_info. stock_module.values[0]== 1):
            print("Configuring stock module...")
            stock_module_config=''
            db.read_stock_table()
            stock_position = position_dist[db.stock_info.position.values[0]]
            #print(stock_position)
            stock_module_config+="\t\t{\n"
            stock_module_config+="\t\t\tmodule: \"third_party/MMM-Stock\",\n"
            stock_module_config+="\t\t\tposition: \"" + stock_position + "\",\n"
            stock_module_config+="\t\t\tconfig: {\n"
            stock_module_config+="\t\t\t\tcompanies: ["

            #print(db.stock_info)
            temp_stock_table = db.stock_info.iloc[:,2:]
            #print (temp_stock_table)
            count = 0
            for col in temp_stock_table.columns:
                if(temp_stock_table[col].values[0] == 1):
                    #print(col)
                    count+=1
                    if(count==1):
                        stock_module_config += "\"" + col + "\""
                    else:
                        stock_module_config += ", \"" + col + "\""
            stock_module_config += "]\n\t\t\t}\n\t\t},\n"
            #print(db.stock_info[db.stock_info.iloc[:,2:]==1])
            #print(stock_module_config)
            file.write(stock_module_config)

        # YouTube Module
        if(db.user_info. youtube_module.values[0]== 1):
            print("Configuring stock module...")
            youtube_module_config=''
            db.read_youtube_table()
            youtube_position = position_dist[db.youtube_info.position.values[0]]
            youtube_video_id = db.youtube_info.video_id.values[0]
            youtube_autoplay = boolean_dist[db.youtube_info.autoplay.values[0]]
            youtube_loop = boolean_dist[db.youtube_info.loop.values[0]]
            #print(youtube_position)
            youtube_module_config+="\t\t{\n"
            youtube_module_config+="\t\t\tmodule: \"third_party/MMM-EmbedYoutube\",\n"
            youtube_module_config+="\t\t\tposition: \"" + youtube_position + "\",\n"
            youtube_module_config+="\t\t\tconfig: {\n"
            youtube_module_config+="\t\t\t\tvideo_id: \"" + youtube_video_id + "\",\n"
            youtube_module_config+="\t\t\t\tautoplay: " + youtube_autoplay + ",\n"
            youtube_module_config+="\t\t\t\tloop: " + youtube_loop + "\n"
            youtube_module_config += "\t\t\t}\n\t\t},\n"

            #print(youtube_module_config)
            file.write(youtube_module_config)

        # Covid Module
        if(db.user_info. covid_module.values[0]== 1):
            db.read_covid_table()
            print("Configuring covid module...")
            covid_module_config=''
            db.read_covid_table()
            covid_position = position_dist[db.covid_info.position.values[0]]
            covid_worldStats = boolean_dist[db.covid_info.worldStats.values[0]]
            covid_updateInterval = str(db.covid_info.updateInterval.values[0])
            #print(covid_worldStats)
            #print(covid_updateInterval)
            covid_module_config+="\t\t{\n"
            covid_module_config+="\t\t\tmodule: \"third_party/MMM-COVID19\",\n"
            covid_module_config+="\t\t\tposition: \"" + covid_position + "\",\n"
            covid_module_config+="\t\t\tconfig: {\n"
            covid_module_config+="\t\t\t\trapidapiKey: \"5abcaaa7a1mshcfa66b90783c879p1d4fd4jsn78fdde0478ef\",\n"
            covid_module_config+="\t\t\t\tworldStats: " + covid_worldStats + ",\n"
            covid_module_config+="\t\t\t\tupdateInterval: " + covid_updateInterval + ", \n"
            covid_module_config+="\t\t\t\tcountries: ["

            temp_covid_table = db.covid_info.iloc[:,4:]
            #print (temp_covid_table)
            count = 0
            for col in temp_covid_table.columns:
                if(temp_covid_table[col].values[0] == 1):
                    #print(col)
                    count+=1
                    if(count==1):
                        covid_module_config += "\"" + col + "\""
                    else:
                        covid_module_config += ", \"" + col + "\""

            covid_module_config += "]\n\t\t\t}\n\t\t},\n"
            #print(covid_module_config)
            file.write(covid_module_config)

        # Helloworld Module
        print("Configuring helloworld module...")
        hello_module_config="\t\t{\n"
        hello_module_config+="\t\t\tmodule: \"helloworld\",\n"
        hello_module_config+="\t\t\tposition: \"bottom_bar\",\n"
        hello_module_config+="\t\t\tconfig: {\n"
        hello_module_config+="\t\t\t\ttext: \"Hello " + user_name + "!\"\n"
        hello_module_config += "\t\t\t}\n\t\t}\n"
        #print(hello_module_config)
        file.write(hello_module_config)

        # Tail text
        tail_text = "\t]\n"
        tail_text += "\n};\n\n"
        tail_text += "/*************** DO NOT EDIT THE LINE BELOW ***************/\n"
        tail_text += "if (typeof module !== \"undefined\") {module.exports = config;}"

        #print(tail_text)
        file.write(tail_text)
        file.close()

        # copy it to config.js and then delete it
        shutil.move(filename,'config.js')
        #os.remove(filename)
if __name__ == '__main__':
    try:
        name = sys.argv[1]
        main(name)
    except:
        print ('Usage: python generate_config.py user_name')
        sys.exit(2)
