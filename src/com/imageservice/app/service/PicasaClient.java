package com.imageservice.app.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaStreamSource;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.StringUtil;
import com.imageservice.app.util.HttpUtil;

public class PicasaClient {

    public static final String        GOOGLE_AUTH_URL    = "https://accounts.google.com/o/oauth2/auth";
    public static final String        GOOGLE_TOKEN_URL   = "https://accounts.google.com/o/oauth2/token";
    public static final String        GOOGLE_PROFILE_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    private String                    clientId           = "PUT_YOUR_CLIENT_ID";
    private String                    clientSecret       = "PUT_YUOR_CLIENT_SECRETE";
    /*
     * Goto below link for generating clientId and clientSecret
     * https://code.google.com/apis/console/
     */

    private static final PicasaClient goolClient         = new PicasaClient();

    public static final PicasaClient getInstance() {
        return goolClient;
    }

    /**
     * This method prepares Google authorization url. User should be redirected
     * to this url for providing permissions
     * 
     * @param redirectUri
     * @param state
     * @return
     */
    public String getAuthorizeUrl(String redirectUri, String state) {
        Map<String, String> params = new HashMap<>();
        params.put("scope", "openid profile email https://picasaweb.google.com/data/");
        params.put("response_type", "code");
        params.put("client_id", this.clientId);
        params.put("state", state);
        params.put("redirect_uri", redirectUri);
        StringBuilder builder = new StringBuilder();
        try {
            for (Map.Entry<String, String> pm : params.entrySet()) {
                builder.append("&").append(URLEncoder.encode(pm.getKey(), "utf-8")).append("=")
                        .append(URLEncoder.encode(pm.getValue(), "utf-8"));
            }
        } catch (UnsupportedEncodingException use) {
            use.printStackTrace();
            return null;
        }
        String queryString = builder.substring(1);
        return GOOGLE_AUTH_URL + "?" + queryString;
    }

    public Map<String, Object> getAccessTokenMap(String code, String redirectUri) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", this.clientId);
        params.put("client_secret", this.clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");
        try {
            HttpUtil.HttpResponse response = HttpUtil.getInstance().makePostRequest(GOOGLE_TOKEN_URL, params);
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(response.getResponseBody());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getUserProfile(String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        try {
            HttpUtil.HttpResponse response = HttpUtil.getInstance().makeGetRequest(GOOGLE_PROFILE_URL, params);
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(response.getResponseBody());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserEmail(String accessToken) {
        Map<String, Object> user = this.getUserProfile(accessToken);
        String email = (String) user.get("email");
        return email;
    }

    public List<String> getUserAlbums(String accessToken) throws IOException {
        PicasawebService picasawebService = new PicasawebService("zigride");
        picasawebService.setAuthSubToken(accessToken, null);
        URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/default?kind=album");
        List<String> albumList = new ArrayList<String>();
        UserFeed myUserFeed;
        try {
            myUserFeed = picasawebService.getFeed(feedUrl, UserFeed.class);
            for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                albumList.add(myAlbum.getTitle().getPlainText());
            }
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
        }
        return albumList;
    }

    public boolean isAlbumExists(String accessToken, String albumName) throws IOException {
        if (StringUtil.isEmpty(albumName))
            return false;
        PicasawebService picasawebService = new PicasawebService("zigride");
        picasawebService.setAuthSubToken(accessToken, null);
        URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/default?kind=album");
        UserFeed myUserFeed;
        try {
            myUserFeed = picasawebService.getFeed(feedUrl, UserFeed.class);
            for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                if (albumName.toLowerCase().equals(myAlbum.getTitle().getPlainText())) {
                    return true;
                }
            }
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
        }
        return false;
    }

    public AlbumEntry getAlbum(String accessToken, String albumName) throws IOException {
        if (StringUtil.isEmpty(albumName)) {
            return null;
        }
        PicasawebService picasawebService = new PicasawebService("zigride");
        picasawebService.setAuthSubToken(accessToken, null);
        URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/default?kind=album");
        UserFeed myUserFeed;
        try {
            myUserFeed = picasawebService.getFeed(feedUrl, UserFeed.class);
            for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                if (albumName.toLowerCase().equals(myAlbum.getTitle().getPlainText())) {
                    return myAlbum;
                }
            }
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }

    public List<List<String>> getImages(String accessToken, String albumName) throws IOException, ServiceException {

        if (StringUtil.isEmpty(albumName)) {
            return null;
        }
        PicasawebService picasawebService = new PicasawebService("zigride");
        picasawebService.setAuthSubToken(accessToken, null);
        URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/default?kind=album");
        AlbumEntry albumEntry = null;
        UserFeed myUserFeed;
        try {
            myUserFeed = picasawebService.getFeed(feedUrl, UserFeed.class);
            for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                if (albumName.toLowerCase().equals(myAlbum.getTitle().getPlainText())) {
                    albumEntry = myAlbum;
                }
            }
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
        }
        if (albumEntry == null) {
            return null;
        }
        feedUrl = new URL(albumEntry.getFeedLink().getHref());

        AlbumFeed feed = picasawebService.getFeed(feedUrl, AlbumFeed.class);
        List<List<String>> images = new ArrayList<List<String>>();
        for (PhotoEntry photoEntry : feed.getPhotoEntries()) {
            List<String> image = new ArrayList<String>();
            if (photoEntry.getMediaContents().size() > 0) {
                image.add(photoEntry.getMediaContents().get(0).getUrl());
                List<MediaThumbnail> thumbnails = photoEntry.getMediaThumbnails();
                for (MediaThumbnail mediaThumbnail : thumbnails) {
                    image.add(mediaThumbnail.getUrl());
                }
            }
            images.add(image);
        }
        if (images.size() == 0) {
            return null;
        }
        return images;
    }

    public String upload(String accessToken, String email, String album, InputStream imageStream, String mimeType) {
        // mime = "image/jpeg" , "image/png"
        String imageUrl = null;
        try {
            // create a new album
            URL postUrl = new URL("https://picasaweb.google.com/data/feed/api/user/default");
            PicasawebService picasawebService = new PicasawebService("zigride");
            picasawebService.setAuthSubToken(accessToken, null);

            AlbumEntry en = new AlbumEntry();
            en.setTitle(new PlainTextConstruct(album));

            AlbumEntry insertedEntry = null;
            AlbumEntry seachEntry = getAlbum(accessToken, album);
            if (seachEntry == null) {
                insertedEntry = picasawebService.insert(postUrl, en);
            } else {
                insertedEntry = seachEntry;
            }

            AlbumFeed fd = insertedEntry.getFeed();

            PhotoEntry photoEntry = new PhotoEntry();

            // upload photos to that album
            MediaStreamSource mediaStream = new MediaStreamSource(imageStream, mimeType);
            photoEntry.setMediaSource(mediaStream);
            photoEntry = fd.insertPhoto(mediaStream);

            if (photoEntry.getMediaContents().size() > 0) {
                List<MediaThumbnail> thumbnails = photoEntry.getMediaThumbnails();
                // System.out.println(thumbnails.size());
                int i = 1;
                for (MediaThumbnail mediaThumbnail : thumbnails) {
                    // System.out.println(mediaThumbnail.getUrl());
                    if (i == 1) {
                        // picasaImage.setThumbnail1(mediaThumbnail.getUrl());
                    } else if (i == 2) {
                        // picasaImage.setThumbnail2(mediaThumbnail.getUrl());
                    } else if (i == 3) {
                        // picasaImage.setThumbnail3(mediaThumbnail.getUrl());
                    }
                    i++;
                }
                /*
                 * MediaGroup mediaGroup = photoEntry.getMediaGroup();
                 * System.out.println(mediaGroup.getContents().size());
                 * for(MediaContent mediaContent : mediaGroup.getContents()) {
                 * System.out.println(mediaContent.getUrl()); }
                 */
                // imageUrl = photoEntry.getMediaContents().get(0).getUrl();
                imageUrl = photoEntry.getMediaContents().get(0).getUrl();
            }

        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (com.google.gdata.util.AuthenticationException e) {
            e.printStackTrace();
        } catch (com.google.gdata.util.ServiceException e) {
            e.printStackTrace();
        }

        return imageUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}
