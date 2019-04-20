package com.rdc.musicplayer.musicplayer.bean;

import java.util.List;

public class SearchBean {

    public SearchResult result;

    public class SearchResult {
        public List<SearchSong> songs;

        public class SearchSong {
            public String name;
            public String id;
            public List<Yanyuan> ar;

            public class Yanyuan {
                public String id;
                public String name;
            }

            public SongPic al;

            public class SongPic {
                public String picUrl;
            }
        }
    }

}
