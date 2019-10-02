/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
 * Copyright (C) 2019 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.data

import com.owncloud.android.domain.exceptions.AccountException
import com.owncloud.android.domain.exceptions.AccountNotFoundException
import com.owncloud.android.domain.exceptions.AccountNotNewException
import com.owncloud.android.domain.exceptions.AccountNotTheSameException
import com.owncloud.android.domain.exceptions.BadOcVersionException
import com.owncloud.android.domain.exceptions.CancelledException
import com.owncloud.android.domain.exceptions.ConflictException
import com.owncloud.android.domain.exceptions.CopyIntoDescendantException
import com.owncloud.android.domain.exceptions.DelayedForWifiException
import com.owncloud.android.domain.exceptions.FileNotFoundException
import com.owncloud.android.domain.exceptions.ForbiddenException
import com.owncloud.android.domain.exceptions.IncorrectAddressException
import com.owncloud.android.domain.exceptions.InstanceNotConfiguredException
import com.owncloud.android.domain.exceptions.InvalidCharacterException
import com.owncloud.android.domain.exceptions.InvalidCharacterInNameException
import com.owncloud.android.domain.exceptions.InvalidLocalFileNameException
import com.owncloud.android.domain.exceptions.InvalidOverwriteException
import com.owncloud.android.domain.exceptions.LocalFileNotFoundException
import com.owncloud.android.domain.exceptions.LocalStorageFullException
import com.owncloud.android.domain.exceptions.LocalStorageNotCopiedException
import com.owncloud.android.domain.exceptions.LocalStorageNotMovedException
import com.owncloud.android.domain.exceptions.LocalStorageNotRemovedException
import com.owncloud.android.domain.exceptions.MoveIntoDescendantException
import com.owncloud.android.domain.exceptions.NoConnectionWithServerException
import com.owncloud.android.domain.exceptions.NoNetworkConnectionException
import com.owncloud.android.domain.exceptions.OAuth2ErrorAccessDeniedException
import com.owncloud.android.domain.exceptions.OAuth2ErrorException
import com.owncloud.android.domain.exceptions.PartialCopyDoneException
import com.owncloud.android.domain.exceptions.PartialMoveDoneException
import com.owncloud.android.domain.exceptions.QuotaExceededException
import com.owncloud.android.domain.exceptions.RedirectToNonSecureException
import com.owncloud.android.domain.exceptions.SSLErrorException
import com.owncloud.android.domain.exceptions.SSLRecoverablePeerUnverifiedException
import com.owncloud.android.domain.exceptions.ServerConnectionTimeoutException
import com.owncloud.android.domain.exceptions.ServerNotReachableException
import com.owncloud.android.domain.exceptions.ServerResponseTimeoutException
import com.owncloud.android.domain.exceptions.ServiceUnavailableException
import com.owncloud.android.domain.exceptions.ShareForbiddenException
import com.owncloud.android.domain.exceptions.ShareNotFoundException
import com.owncloud.android.domain.exceptions.ShareWrongParameterException
import com.owncloud.android.domain.exceptions.SpecificForbiddenException
import com.owncloud.android.domain.exceptions.SpecificMethodNotAllowedException
import com.owncloud.android.domain.exceptions.SpecificServiceUnavailableException
import com.owncloud.android.domain.exceptions.SpecificUnsupportedMediaTypeException
import com.owncloud.android.domain.exceptions.SyncConflictException
import com.owncloud.android.domain.exceptions.UnauthorizedException
import com.owncloud.android.domain.exceptions.UnhandledHttpCodeException
import com.owncloud.android.domain.exceptions.UnknownErrorException
import com.owncloud.android.domain.exceptions.WrongServerResponseException
import com.owncloud.android.lib.common.OwnCloudClient
import com.owncloud.android.lib.common.operations.RemoteOperation
import com.owncloud.android.lib.common.operations.RemoteOperationResult
import java.net.SocketTimeoutException

fun <T> awaitToRemoteOperationResult(remoteOperation: RemoteOperation<T>, client: OwnCloudClient): T {
    remoteOperation.execute(client).also {
        return handleRemoteOperationResult(it, remoteOperation)
    }
}

fun <T> waitForRemoteOperationResult(remoteOperation: RemoteOperation<T>, client: OwnCloudClient): T {
    remoteOperation.execute(client).also {
        return handleRemoteOperationResult(it, remoteOperation)
    }
}

private fun <T> handleRemoteOperationResult(
    remoteOperationResult: RemoteOperationResult<T>,
    remoteOperation: RemoteOperation<T>
): T {
    if (remoteOperationResult.isSuccess) {
        return remoteOperationResult.data
    }

    when (remoteOperationResult.code) {
        RemoteOperationResult.ResultCode.FORBIDDEN -> {

        }

        RemoteOperationResult.ResultCode.WRONG_CONNECTION -> throw NoConnectionWithServerException()
        RemoteOperationResult.ResultCode.TIMEOUT -> {
            if (remoteOperationResult.exception is SocketTimeoutException) throw ServerResponseTimeoutException()
            else throw ServerConnectionTimeoutException()
        }
        RemoteOperationResult.ResultCode.HOST_NOT_AVAILABLE -> throw ServerNotReachableException()
        RemoteOperationResult.ResultCode.UNHANDLED_HTTP_CODE -> throw UnhandledHttpCodeException()
        RemoteOperationResult.ResultCode.UNAUTHORIZED -> throw UnauthorizedException()
        RemoteOperationResult.ResultCode.FILE_NOT_FOUND -> throw FileNotFoundException()
        RemoteOperationResult.ResultCode.INSTANCE_NOT_CONFIGURED -> throw InstanceNotConfiguredException()
        RemoteOperationResult.ResultCode.UNKNOWN_ERROR -> throw UnknownErrorException()
        RemoteOperationResult.ResultCode.INCORRECT_ADDRESS -> throw IncorrectAddressException()
        RemoteOperationResult.ResultCode.NO_NETWORK_CONNECTION -> throw NoNetworkConnectionException()
        RemoteOperationResult.ResultCode.SSL_ERROR -> throw SSLErrorException()
        RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED -> throw SSLRecoverablePeerUnverifiedException()
        RemoteOperationResult.ResultCode.BAD_OC_VERSION -> throw BadOcVersionException()
        RemoteOperationResult.ResultCode.CANCELLED -> throw CancelledException()
        RemoteOperationResult.ResultCode.INVALID_LOCAL_FILE_NAME -> throw InvalidLocalFileNameException()
        RemoteOperationResult.ResultCode.INVALID_OVERWRITE -> throw InvalidOverwriteException()
        RemoteOperationResult.ResultCode.CONFLICT -> throw ConflictException()
        RemoteOperationResult.ResultCode.OAUTH2_ERROR -> throw OAuth2ErrorException()
        RemoteOperationResult.ResultCode.SYNC_CONFLICT -> throw SyncConflictException()
        RemoteOperationResult.ResultCode.LOCAL_STORAGE_FULL -> throw LocalStorageFullException()
        RemoteOperationResult.ResultCode.LOCAL_STORAGE_NOT_MOVED -> throw LocalStorageNotMovedException()
        RemoteOperationResult.ResultCode.LOCAL_STORAGE_NOT_COPIED -> throw LocalStorageNotCopiedException()
        RemoteOperationResult.ResultCode.OAUTH2_ERROR_ACCESS_DENIED -> throw OAuth2ErrorAccessDeniedException()
        RemoteOperationResult.ResultCode.QUOTA_EXCEEDED -> throw QuotaExceededException()
        RemoteOperationResult.ResultCode.ACCOUNT_NOT_FOUND -> throw AccountNotFoundException()
        RemoteOperationResult.ResultCode.ACCOUNT_EXCEPTION -> throw AccountException()
        RemoteOperationResult.ResultCode.ACCOUNT_NOT_NEW -> throw AccountNotNewException()
        RemoteOperationResult.ResultCode.ACCOUNT_NOT_THE_SAME -> throw AccountNotTheSameException()
        RemoteOperationResult.ResultCode.INVALID_CHARACTER_IN_NAME -> throw InvalidCharacterInNameException()
        RemoteOperationResult.ResultCode.SHARE_NOT_FOUND -> throw ShareNotFoundException()
        RemoteOperationResult.ResultCode.LOCAL_STORAGE_NOT_REMOVED -> throw LocalStorageNotRemovedException()
        RemoteOperationResult.ResultCode.FORBIDDEN -> throw ForbiddenException()
        RemoteOperationResult.ResultCode.SHARE_FORBIDDEN -> throw ShareForbiddenException()
        RemoteOperationResult.ResultCode.SPECIFIC_FORBIDDEN -> throw SpecificForbiddenException()
        RemoteOperationResult.ResultCode.OK_REDIRECT_TO_NON_SECURE_CONNECTION -> throw RedirectToNonSecureException()
        RemoteOperationResult.ResultCode.INVALID_MOVE_INTO_DESCENDANT -> throw MoveIntoDescendantException()
        RemoteOperationResult.ResultCode.INVALID_COPY_INTO_DESCENDANT -> throw CopyIntoDescendantException()
        RemoteOperationResult.ResultCode.PARTIAL_MOVE_DONE -> throw PartialMoveDoneException()
        RemoteOperationResult.ResultCode.PARTIAL_COPY_DONE -> throw PartialCopyDoneException()
        RemoteOperationResult.ResultCode.SHARE_WRONG_PARAMETER -> throw ShareWrongParameterException()
        RemoteOperationResult.ResultCode.WRONG_SERVER_RESPONSE -> throw WrongServerResponseException()
        RemoteOperationResult.ResultCode.INVALID_CHARACTER_DETECT_IN_SERVER -> throw InvalidCharacterException()
        RemoteOperationResult.ResultCode.DELAYED_FOR_WIFI -> throw DelayedForWifiException()
        RemoteOperationResult.ResultCode.LOCAL_FILE_NOT_FOUND -> throw LocalFileNotFoundException()
        RemoteOperationResult.ResultCode.SERVICE_UNAVAILABLE -> throw ServiceUnavailableException()
        RemoteOperationResult.ResultCode.SPECIFIC_SERVICE_UNAVAILABLE -> throw SpecificServiceUnavailableException()
        RemoteOperationResult.ResultCode.SPECIFIC_UNSUPPORTED_MEDIA_TYPE -> throw SpecificUnsupportedMediaTypeException()
        RemoteOperationResult.ResultCode.SPECIFIC_METHOD_NOT_ALLOWED -> throw SpecificMethodNotAllowedException()
        else -> throw Exception()
    }
}