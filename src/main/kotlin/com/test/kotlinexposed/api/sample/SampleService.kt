package com.test.kotlinexposed.api.sample

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SampleService(
    private val sampleRepository: SampleRepository
) {
    @Transactional(readOnly = true)
    fun getSampleList(request: SampleRequestDto): List<SampleResponseDto> {
        return sampleRepository.getSampleList(request)
            .map { SampleResponseDto(it) }
    }

    @Transactional(readOnly = true)
    fun getSampleListCount(request: SampleRequestDto): Long {
        return sampleRepository.getSampleListCount(request)
    }

    @Transactional(readOnly = true)
    fun getSample(sampleId: String): SampleResponseDto? {
        return sampleRepository.getSample(sampleId)
            ?.let { SampleResponseDto(it) }
    }

    @Transactional
    fun insertSample(request: SampleRequestDto) {
        sampleRepository.insertSample(request)
    }

    @Transactional
    fun updateSample(request: SampleRequestDto) {
        sampleRepository.updateSample(request)
    }

    @Transactional
    fun deleteSample(sampleId: String) {
        sampleRepository.deleteSample(sampleId)
    }

    @Transactional(readOnly = true)
    fun getSampleListNative(request: SampleRequestDto): List<SampleResponseDto> {
        return sampleRepository.getSampleListNative(request)
    }
}