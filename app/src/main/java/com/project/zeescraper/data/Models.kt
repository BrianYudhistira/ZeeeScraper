package com.project.zeescraper.data

data class CharacterList(
    val id: Int,
    val name: String,
    val link: String,
    val image: String,
    val element: String,
    val element_picture: String,
    val tier: String
)

data class CharacterDetail(
    val w_engines: List<WEngine>,
    val disk_drives: List<DiskDrive>,
    val best_disk_drive_stats: List<BestDiskDriveStat>,
    val substats: String,
    val endgame_stats: String
)

data class WEngine(
    val build_name: String,
    val build_s: String,
    val w_engine_picture: String,
    val detail: String
)

data class DiskDrive(
    val name: String,
    val detail_2pc: String,
    val detail_4pc: String,
    val image_link: String
)

data class BestDiskDriveStat(
    val disk_number: String,
    val disk_description: String
)

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null,
)

data class ApiResponseDetail<T>(
    val status: String,
    val message: String,
    val data: T? = null,
)
