/**
 * Created by rabeeaahmad on 2017-05-10.
 */
package analytics.server.grails

import grails.rest.*

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest

class AuthController extends RestfulController<User> {

    /*
    *  OAUTH AUTHORIZATION CODE FLOW                                     _ _ _ _ _
    *  1. Request token from google (user login & consent)      --->    |         |
    *  2. Retrieve an authorization code                        <---    | Google  |
    *  3. Exchange the code for a token from google             --->    |   API   |
    *  4. Retrieve the token response                           <---    |         |
    *  5. User is authenticated!!                                       |_ _ _ _ _|
    * */

    static CLIENT_ID = '< insert client id here >'
    static CLIENT_SECRET = '< insert client secret here >'
    static REDIRECT_URI = 'http://localhost:9000/login'

    static SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/plus.login",
            "https://www.googleapis.com/auth/plus.profile.emails.read")

    static USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token"


    static HttpTransport HTTP_TRANSPORT = new NetHttpTransport()
    static JsonFactory JSON_FACTORY = new JacksonFactory()
    static GoogleAuthorizationCodeFlow flow

    static responseFormats = ['json', 'xml']

    AuthController() {
        super(User)
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
                .setAccessType("offline")
                .build()
    }

    @Override
    def index() {
        respond User.list()
    }

    /* Generates the token request url
     * @returns: {String} url
     * Step 1 of oauth flow
     * */
    def login() {
        try {
            GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl()
            def data = url.setRedirectUri(REDIRECT_URI).build()
            render data
        }
        catch (e) {
            log.error("Error: ", e)
        }
    }


    /* Exchanges auth code for an access token, and then looks up user info with access token
     * @param {String} code
     * @returns: {Object} = gplus id, email, name, given name, family name, link to profile, picture, locale, hd
     * Step 3 & 4 of oauth flow
     * */

    def getUserInfo() {
        def authCode = params.code

        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, authCode, REDIRECT_URI)
                    .execute()

            session.token = tokenResponse.getAccessToken()

            Credential credential = flow.createAndStoreCredential(tokenResponse, null)
            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential)

            GenericUrl url = new GenericUrl(USER_INFO_URL)
            HttpRequest request = requestFactory.buildGetRequest(url)
            request.getHeaders().setContentType('application/json')
            def userData = request.execute().parseAsString()
            userData.a
            render userData
        }
        catch(e) {
           render e
        }
    }