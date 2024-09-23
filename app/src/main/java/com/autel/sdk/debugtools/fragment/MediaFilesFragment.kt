package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.autel.drone.demo.R
import com.autel.drone.demo.databinding.FragmentMideaFilesBinding
import com.autel.drone.sdk.http.BaseRequest
import com.autel.drone.sdk.libbase.error.AutelStatusCode
import com.autel.drone.sdk.libbase.error.IAutelCode
import com.autel.drone.sdk.vmodelx.interfaces.file.FileListener
import com.autel.drone.sdk.vmodelx.manager.AlbumManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.bean.AlbumFolderResultBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.bean.AlbumResultBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.bean.AutelMediaBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.MediaTypeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.OrderTypeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.StorageTypeEnum
import com.autel.drone.sdk.vmodelx.module.fileservice.AutelDroneFile
import com.autel.drone.sdk.vmodelx.module.fileservice.AutelFileUtil
import com.autel.drone.sdk.vmodelx.module.fileservice.FileTransmitListener
import com.autel.drone.sdk.vmodelx.module.fileservice.FolderQueryListener
import com.autel.drone.sdk.vmodelx.module.fileservice.QueryFileListBean
import com.autel.drone.sdk.vmodelx.utils.MicroFtpUtil
import com.autel.drone.sdk.vmodelx.utils.S3DownloadInterceptor
import com.autel.sdk.debugtools.KeyValueDialogUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * all media files status generate with drone
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */
class MediaFilesFragment : AutelFragment() {

    companion object {
        private const val TAG = "MediaFilesFragment"
        private const val LISTEN_RECORD_MAX_LENGTH = 6000
    }

    private lateinit var binding: FragmentMideaFilesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMideaFilesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.tvResult.movementMethod = ScrollingMovementMethod.getInstance()

        binding.tvUpload.setOnClickListener {//上传文件
            Log.e(TAG, "点击了上传文件")
            binding.tvUpload.isClickable = false
            binding.tvResult.text = "start upload"
            lifecycleScope.launch(Dispatchers.IO){
                var path = copyToSdcard()
                delay(500)
                with(Dispatchers.Main){
                    var file = File(path)
                    if (!file.exists()) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                getString(R.string.debug_file_does_not_exist),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.tvUpload.isClickable = true
                        }
                        return@launch
                    }

                    MicroFtpUtil.uploadMissionFile(file,  object :
                        FileTransmitListener<String> {
                        override fun onSuccess(result: String?) {
                            Log.e(TAG, "onSuccess")
                            lifecycleScope.launch(Dispatchers.Main) {
                                result.also { binding.tvResult.text = "$it, upload success" }
                                binding.tvUpload.isClickable = true
                            }
                        }

                        override fun onProgress(sendLength: Long, totalLength: Long, speed: Long) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                (String.format(
                                    getString(R.string.debug_current_upload_size_bytes),
                                    sendLength
                                )).also { binding.tvResult.text = it }
                                binding.tvUpload.isClickable = true
                            }
                        }

                        override fun onFailed(code: IAutelCode?, msg: String?) {
                            Log.e(TAG, "onFailed $code")
                            lifecycleScope.launch(Dispatchers.Main) {
                                code.also { binding.tvResult.text = it.toString() }
                                binding.tvUpload.isClickable = true
                            }
                        }
                    })
                }
            }
        }
        binding.tvDownload.setOnClickListener {
            Log.e(TAG, "点击了下载文件")
            "".also { binding.tvResult.text = it }
            var desPath = "/sdcard/fise/1665107840"
            var sourcePath = "/mission/1665107840"
            AutelFileUtil.download(sourcePath, desPath, object :
                FileListener<File> {
                override fun onSuccess(result: File) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        (result.name + " " + result.absolutePath).also {
                            binding.tvResult.text = it
                        }
                    }
                }

                override fun onProgress(sendLength: Long, totalLength: Long) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        (String.format(
                            getString(R.string.debug_schedule),
                            sendLength
                        )).also { binding.tvResult.text = it }
                    }
                }

                override fun onFailed(msg: String) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        msg.also { binding.tvResult.text = it }
                    }
                }
            })
        }
        binding.tvQuery.setOnClickListener {
            Log.e(TAG, "点击了查询文件")
            "".also { binding.tvResult.text = it }
            var sourcePath = "/mission/1665107840"
            AutelFileUtil.queryFile(sourcePath, object : FolderQueryListener {

                override fun onResponse(beans: QueryFileListBean?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        ("$beans").also { binding.tvResult.text = it }
                    }
                }

                override fun onFail(code: Int) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        ("" + code).also { binding.tvResult.text = it }
                    }
                }
            })
        }
        binding.tvDelete.setOnClickListener {
            Log.e(TAG, "点击了删除文件")
            val path = "/mission/1665107840"
            "".also { binding.tvResult.text = it }
            AutelFileUtil.deleteFile(path, object :
                CommonCallbacks.CompletionCallbackWithParam<AutelDroneFile> {
                override fun onSuccess(t: AutelDroneFile?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.tvResult.text = getString(R.string.debug_result_code) + t?.message
                    }
                }

                override fun onFailure(error: IAutelCode, msg: String?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val acode: AutelStatusCode = error as AutelStatusCode;
                        ("" + acode.code + " " + acode.msg).also { binding.tvResult.text = it }
                    }
                }
            })
        }
        binding.btnClearlog.setOnClickListener {
            binding.tvResult.text = ""
            logMessage.delete(0, logMessage.length)
        }

        binding.tvGetMediaList.setOnClickListener {
            val str = Gson().toJson(
                AlbumRequest(
                    MediaTypeEnum.MEDIA_ALL,
                    StorageTypeEnum.EMMC, "", 0, 10, OrderTypeEnum.NORMAL
                )
            )
            KeyValueDialogUtil.showInputDialog(
                requireActivity(),  getString(R.string.debug_set_request_parameters), str, "", false
            ) {
                binding.tvResult.text = appendLogMessageRecord(it)
                requestMediaFileList(Gson().fromJson(it, AlbumRequest::class.java))
            }
        }

        binding.tvGetAlbumFolderList.setOnClickListener {
            val str = Gson().toJson(
                AlbumFolderRequest(StorageTypeEnum.EMMC, OrderTypeEnum.NORMAL)
            )
            KeyValueDialogUtil.showInputDialog(
                requireActivity(),  getString(R.string.debug_set_request_parameters), str, "", false
            ) {
                binding.tvResult.text = appendLogMessageRecord(it)
                requestMediaFolderList(Gson().fromJson(it, AlbumFolderRequest::class.java))
            }
        }

        binding.tvAlbumFileDel.setOnClickListener {
            val str = Gson().toJson(
                DelRequest(0)
            )
            KeyValueDialogUtil.showInputDialog(
                requireActivity(),  getString(R.string.debug_set_request_parameters), str, "", false
            ) {
                binding.tvResult.text = appendLogMessageRecord(it)
                requestDelAlbumFile(Gson().fromJson(it, DelRequest::class.java))
            }
        }
        binding.tvAlbumFileDownload.setOnClickListener {
            val str = Gson().toJson(
                FileRequest("")
            )
            KeyValueDialogUtil.showInputDialog(
                requireActivity(),  getString(R.string.debug_set_request_parameters), str, "", false
            ) {
                binding.tvResult.text = appendLogMessageRecord(it)
                requestDownloadAlbumFile(Gson().fromJson(it, FileRequest::class.java))
            }
        }

        binding.tvAlbumFileCancelDownload.setOnClickListener {
            val str = Gson().toJson(
                FileRequest("")
            )
            KeyValueDialogUtil.showInputDialog(
                requireActivity(),  getString(R.string.debug_set_request_parameters), str, "", false
            ) {
                binding.tvResult.text = appendLogMessageRecord(it)
                requestCancelDownload()
            }
        }
        binding.tvHealthCheck.setOnClickListener {
            binding.tvResult.text =
                appendLogMessageRecord(getString(R.string.debug_health_check_request))
            /*MicroFtpUtil.healthCheck(object : CommonCallbacks.CompletionCallbackWithParam<String> {
                override fun onSuccess(t: String?) {
                    activity?.runOnUiThread {
                        binding.tvResult.text = appendLogMessageRecord(t)
                    }
                }

                override fun onFailure(error: IAutelCode, msg: String?) {
                    val str =
                        getString(R.string.debug_code) + error.code + ", " + getString(R.string.debug_message) + msg
                    activity?.runOnUiThread {
                        binding.tvResult.text = appendLogMessageRecord(str)
                    }
                }
            })*/
        }

    }

    val pos = 1
    var baseRequest: BaseRequest? = null
    var s3DownloadInterceptor : S3DownloadInterceptor? = null

    private fun requestCancelDownload() {
        baseRequest?.let { AlbumManager.getAlbumManager().cancelDownload(it) }
        s3DownloadInterceptor?.let { it.cancel() }
    }

    private fun requestDownloadAlbumFile(request: FileRequest) {
        if (!fileList.isNullOrEmpty() && fileList!!.isNotEmpty()) {
            var sourceFile = fileList!![pos].getOriginPath()

            var destFile = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            var saveFile = destFile!!.absolutePath + "/" + fileList!![pos].name

            Log.e(TAG, "sourceFile $sourceFile")
            Log.e(TAG, "destFile $saveFile")

            s3DownloadInterceptor = AlbumManager.getAlbumManager().downloadMediaFileNew(
                sourceFile,
                saveFile,
                object : CommonCallbacks.DownLoadCallbackWithProgress<Double> {

                    override fun onSuccess(file: File?) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.tvResult.text =
                                getString(R.string.debug_success) + " ${file!!.absolutePath}"
                        }
                    }

                    override fun onProgressUpdate(progress: Double, speed: Double) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (isAdded) {
                                binding.tvResult.text =  getString(R.string.debug_progress) + progress + ", " + getString(R.string.debug_speed) + speed
                            }
                        }
                    }

                    override fun onFailure(error: IAutelCode) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            val acode: AutelStatusCode = error as AutelStatusCode;
                            ("" + acode.code + " " + acode.msg).also { binding.tvResult.text = it }
                        }
                        Log.e(TAG, "onFailure ${error.code}, ${error.toString()}")
                    }
                })
        }
    }

    private fun requestDelAlbumFile(request: DelRequest) {
        Log.e(TAG, "requestDelAlbumFile ${request.index}")
        AlbumManager.getAlbumManager()
            .deleteMediaFile(request.index, object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.tvResult.text = getString(R.string.debug_success)
                    }
                }

                override fun onFailure(error: IAutelCode, msg: String?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val acode: AutelStatusCode = error as AutelStatusCode;
                        ("" + acode.code + " " + acode.msg).also { binding.tvResult.text = it }
                    }
                }
            })
    }

    var fileList: List<AutelMediaBean>? = mutableListOf()


    private fun requestMediaFileList(request: AlbumRequest) {
        binding.tvResult.text = appendLogMessageRecord(getString(R.string.debug_ask))
        AlbumManager.getAlbumManager().getMediaFileList(request.type,
            request.storageType,
            request.albumName,
            request.offset,
            request.count,
            request.order,
            object : CommonCallbacks.CompletionCallbackWithParam<AlbumResultBean> {
                override fun onSuccess(t: AlbumResultBean?) {
                    fileList = t!!.pathlist
                    Log.d(TAG, "totalCnt=${t.totalCnt}")
                    Log.d(TAG, "fileList size=${fileList?.size}")
                    fileList?.forEach {
                        Log.d(
                            TAG,
                            "${it.mediaType}, size=${it.size}, index(id)=${it.index} ,name=${it.name}"
                        )

                        if ("Package".equals(it.name)) {
                            Log.e(TAG, "Package ====Thumbnail${it.getThumbnailPath()}")
                            Log.e(TAG, "Package ====size =${it.filelist.size}")
                            it.filelist.forEach {
                                Log.d(
                                    TAG,
                                    "${it.mediaType}, size=${it.size}, index(id)=${it.index} ,name=${it.name}"
                                )
                                if (it.mediaType != 0) {
                                    Log.i(TAG, "Thumbnail=${it.getThumbnailPath()}")
                                    Log.i(TAG, "OriginPath=${it.getOriginPath()}")
                                    Log.i(TAG, "PreviewPath= ${it.getPreviewPath()}")
                                    Log.i(TAG, "ScreennailPath=${it.getScreennailPath()}")
                                } else {
                                    Log.i(TAG, "OriginPath =${it.getOriginPath()}")
                                }
                            }
                            Log.e(TAG, "Package === =end")
                        } else {
                            if (it.mediaType != 0) {
                                Log.d(TAG, "Thumbnail=${it.getThumbnailPath()}")
                                Log.d(TAG, "OriginPath=${it.getOriginPath()}")
                                Log.d(TAG, "PreviewPath= ${it.getPreviewPath()}")
                                Log.d(TAG, "ScreennailPath=${it.getScreennailPath()}")
                            } else {
                                Log.d(TAG, "OriginPath =${it.getOriginPath()}")
                            }
                        }
                    }
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.tvResult.text = appendLogMessageRecord(t?.toString())
                    }
                }

                override fun onFailure(error: IAutelCode, msg: String?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val str =
                            getString(R.string.debug_code) + error.code + ", " + getString(R.string.debug_message) + msg
                        binding.tvResult.text = appendLogMessageRecord(str)
                    }
                }
            })
    }

    private fun requestMediaFolderList(request: AlbumFolderRequest) {
        binding.tvResult.text = appendLogMessageRecord(getString(R.string.debug_ask))
        AlbumManager.getAlbumManager().getMediaFolderList(request.storageType, request.sortType,
            object : CommonCallbacks.CompletionCallbackWithParam<AlbumFolderResultBean> {
                override fun onSuccess(t: AlbumFolderResultBean?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        var result = t?.let { t.folderList }
                        binding.tvResult.text = appendLogMessageRecord(result.toString())
                    }
                }

                override fun onFailure(error: IAutelCode, msg: String?) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val str =
                            getString(R.string.debug_code) + error.code + ", " + getString(R.string.debug_message) + msg
                        binding.tvResult.text = appendLogMessageRecord(str)
                    }
                }
            })

    }


    private val logMessage = StringBuilder()
    private fun appendLogMessageRecord(appendStr: String?): String {
        val curTime = SimpleDateFormat("HH:mm:ss").format(Date())
        logMessage.append(curTime)
            .append(":")
            .append(appendStr)
            .append("\n")

        //长度限制
        var result = logMessage.toString()
        if (result.length > LISTEN_RECORD_MAX_LENGTH) {
            result = result.substring(result.length - LISTEN_RECORD_MAX_LENGTH)
        }
        return result
    }

    data class AlbumRequest(
        var type: MediaTypeEnum,
        var storageType: StorageTypeEnum,
        var albumName: String? = " ",
        var offset: Int,
        var count: Int,
        var order: OrderTypeEnum
    )

    data class AlbumFolderRequest(
        var storageType: StorageTypeEnum,
        var sortType: OrderTypeEnum
    )

    data class DelRequest(
        var index: Int,
    )

    data class FileRequest(
        var fileName: String,
    )


    private fun copyToSdcard(): String {
        val sourceFilePath = "1698822367"
        val targetDir =  "${context?.filesDir}/mission_test/"
        val dir = File(targetDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val targetFilePath = targetDir + "1698822367"

        try {
            val inputStream: InputStream = requireActivity().assets.open(sourceFilePath)
            val outputStream: OutputStream = FileOutputStream(targetFilePath)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return targetFilePath
    }

}
