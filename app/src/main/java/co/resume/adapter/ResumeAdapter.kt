package co.resume.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.craft.myresume.R
import app.craft.myresume.databinding.LayoutResumeItemBinding
import com.craft.resumebuilder.tinydb.TinyDB
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResumeAdapter(
    private var resumeList: ArrayList<String>,
    private val tinyDB: TinyDB,
    private val onViewClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ResumeAdapter.ResumeViewHolder>() {

    inner class ResumeViewHolder(val binding: LayoutResumeItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumeViewHolder {
        val binding =
            LayoutResumeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResumeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResumeViewHolder, position: Int) {
        val resumeId = resumeList[position]
        val name = tinyDB.getString("$resumeId:name")
        val designation = tinyDB.getString("$resumeId:descriptionDesignation")
        val profileImage = tinyDB.getString("$resumeId:profile_photo")

        holder.binding.apply {
            resumeName.text = name
            resumeDesignation.text = designation
            resumeDate.text = convertToDayMonthYear(resumeId)
            resumeImage.setImageResource(R.drawable.ic_new_resume_icon)

            if (!profileImage.isNullOrEmpty()) {
                val decodedBytes = Base64.decode(profileImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                resumeImage.setImageBitmap(bitmap)
            } else {
                val byteArrayOutputStream = ByteArrayOutputStream()
                BitmapFactory.decodeResource(root.context.resources, R.drawable.ic_new_resume_icon)
                    .compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

                val encodedImage =
                    Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
                val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                resumeImage.setImageBitmap(bitmap)
            }

            deleteResume.setOnClickListener { onDeleteClick(resumeId) }
            cardView.setOnClickListener { onViewClick(resumeId) }
        }
    }

    override fun getItemCount(): Int = resumeList.size

    fun updateData(newList: ArrayList<String>) {
        resumeList.clear()
        resumeList.addAll(newList)
        notifyDataSetChanged()
    }

    fun convertToDayMonthYear(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
            val date: Date? = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}
