package com.daengnyangffojjak.dailydaengnyang.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordFileResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.RecordFile;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.FileException;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordFileRepository;
import com.daengnyangffojjak.dailydaengnyang.utils.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RecordFileService {

	private final AmazonS3Client amazonS3Client;
	private final Validator validator;
	private final RecordFileRepository recordFileRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Transactional
	public RecordFileResponse uploadRecordFiles(Long petId, Long recordId,
			List<MultipartFile> multipartFiles,
			String userName) {

		// 빈 파일이거나 파일을 업로드 안했을 때 예외 발생
		validator.validateFile(multipartFiles);

		// 일기가 없는 경우 예외발생
		Record record = validator.getRecordById(recordId);

		// 원본 파일 이름 리스트
		List<String> originalFileNameList = new ArrayList<>();
		/// 저장될 파일 이름 리스트
		List<String> storedFileNameList = new ArrayList<>();

		multipartFiles.forEach(file -> {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(file.getContentType());
			objectMetadata.setContentLength(file.getSize());

			String originalFilename = file.getOriginalFilename();

			// file 형식이 잘못된 경우를 확인
			int index;
			try {
				index = originalFilename.lastIndexOf(".");
			} catch (StringIndexOutOfBoundsException e) {
				throw new FileException(ErrorCode.WRONG_FILE_FORMAT);
			}

			// 확장자
			String ext = originalFilename.substring(index + 1);

			// 저장될 파일 이름
			String storedFileName = UUID.randomUUID() + "." + ext;

			// 저장할 디렉토리 경로 + 파일 이름
			String key = "records/" + storedFileName;

			try (InputStream inputStream = file.getInputStream()) {
				amazonS3Client.putObject(
						new PutObjectRequest(bucket, key, inputStream, objectMetadata)
								.withCannedAcl(CannedAccessControlList.PublicRead));
			} catch (IOException e) {
				throw new FileException(ErrorCode.FILE_UPLOAD_ERROR);
			}

			String storeFileUrl = amazonS3Client.getUrl(bucket, key).toString();
			RecordFile recordFile = RecordFile.makeRecordFile(originalFilename, storeFileUrl,
					record);
			recordFileRepository.save(recordFile);

			storedFileNameList.add(storedFileName);
			originalFileNameList.add(originalFilename);
		});

		return RecordFileResponse.ofUpload(originalFileNameList, storedFileNameList);
	}

	@Transactional
	public RecordFileResponse deleteRecordFile(Long petId, Long recordId, Long recordFileId,
			String username) {

		// 일기에 파일이 있는 지 확인
		RecordFile recordFile = validator.getRecordFileById(recordFileId);

		recordFileRepository.delete(recordFile);
		return RecordFileResponse.ofDeleted(recordFileId, "파일 삭제 완료");

	}
}
