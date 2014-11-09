package oauth

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.joda.time.format.DateTimeParser

import java.text.DateFormat
import java.text.MessageFormat
import org.apache.ivy.plugins.conflict.ConflictManager

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
//import org.apache.http.entity.mime.HttpMultipartMode
//import org.apache.http.entity.mime.MultipartEntity
//import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.DefaultHttpClient

import grails.converters.*
import oauth.exceptions.NotFoundException
import oauth.exceptions.ConflictException
import oauth.exceptions.BadRequestException


class OauthService {

    def grailsApplication = new DefaultGrailsApplication()
    def domainMainUsers = grailsApplication.config.domainMainUsers
    def restService

    def PERMISSIONS_MAP = [
            admin:"A",
            normal:"P,AIPM,MIPM",
            developer:"P,AIPM,MIPM,AIPA",
            manager:"P,AIPM,MIPM"
    ]

    def origin      = "MB"

    /*
    access_info_private_me = AIPM
    modify_info_private_me = MIPM
    modify_info_private_all= MIPA
    access_info_private_all= AIPA
    publish                = P
    all                    = A


     */

    def access(def jsonAccess){


        def email       = jsonAccess?.email
        def password    = jsonAccess?.password

        if(!email){
            throw new BadRequestException("You must provider the email")
        }

        if(!password){
            throw new BadRequestException("You must provider the password")
        }

        def userId
        def token
        def status
        def userType
        def permissions

        def queryParams = [

                admin:"MB-ADMIN_123456KKAADPZ"
        ]

        def body = [

                email:email,
                password:password
        ]

        def result = restService.postResource("/users/search/", queryParams, body).data

        if (!result){
            throw  new NotFoundException("The credentials not found")
        }

        userId = result.user_id
        status = result.status
        userType = result.user_type

        permissions = PERMISSIONS_MAP[userType]

        if(!permissions){

            throw new NotFoundException("The permissions not found")
        }

        token = buildToken(userId, status, userType)

        def tokenExpirationDate = new Date() + 1


        Map jsonResult = [:]

        jsonResult.access_token             = token
        jsonResult.token_expiration_date    = tokenExpirationDate.format('MM-dd-yyyy')
        //jsonResult.access_token_e         = token.encodeAsSecure()
        //jsonResult.access_token_d           = token.decodeSecure().toString()
        jsonResult.user_id                  = userId
        jsonResult.user_type                = userType

        jsonResult


    }

    def buildToken(def userId, def status, def userType){

        def token
        def cal     = Calendar.instance
        def dayYear = cal.get(Calendar.DAY_OF_YEAR)

        token = userId+"-"+status+"-"+dayYear+"-"+userType
        token = token.encodeAsSecure()
        token = (origin+"_"+token+"_"+userId).toString()

        token

    }
}
