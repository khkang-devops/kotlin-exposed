package com.test.kotlinexposed.api.sample

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SampleController(
    private val sampleService: SampleService
) {
    @PostMapping("/api/samples/list")
    fun getSampleList(@RequestBody request: SampleRequestDto): ResponseEntity<List<SampleResponseDto>> {
        return ResponseEntity.ok(sampleService.getSampleList(request))
    }

    @PostMapping("/api/samples/list/count")
    fun getSampleListCount(@RequestBody request: SampleRequestDto): ResponseEntity<Long> {
        return ResponseEntity.ok(sampleService.getSampleListCount(request))
    }

    @GetMapping("/api/samples/{id}")
    fun getSample(@PathVariable id: String): ResponseEntity<SampleResponseDto> {
        return ResponseEntity.ok(sampleService.getSample(id))
    }

    @PostMapping("/api/samples/insert")
    fun insertSample(@RequestBody request: SampleRequestDto): ResponseEntity<Void> {
        sampleService.insertSample(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/api/samples/update")
    fun updateSample(@RequestBody request: SampleRequestDto): ResponseEntity<Void> {
        sampleService.updateSample(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/api/samples/delete/{id}")
    fun deleteSample(@PathVariable id: String): ResponseEntity<Void> {
        sampleService.deleteSample(id)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/api/samples/list/native")
    fun getSampleListNative(@RequestBody request: SampleRequestDto): ResponseEntity<List<SampleResponseDto>> {
        return ResponseEntity.ok(sampleService.getSampleListNative(request))
    }
}