package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordFileResponse;
import com.daengnyangffojjak.dailydaengnyang.service.RecordFileService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(RecordFileRestController.class)
class RecordFileRestControllerTest extends ControllerTest {

	@MockBean
	RecordFileService recordFileService;

	@Nested
	@DisplayName("파일 업로드")
	class FileUpload {

		@Test
		@DisplayName("파일 업로드 성공")
		void upload_file_success() throws Exception {

			MultipartFile uploadFile = new MockMultipartFile("댕냥", "댕냥.jpeg", "image/jpeg",
					"".getBytes());
			List<MultipartFile> uploadFiles = List.of(uploadFile);
			String uploadFileName = "댕냥.jpeg";
			List<String> uploadFileNames = List.of(uploadFileName);
			String S3StoredFileName = "skdflsd.jpeg";
			List<String> S3StoredFilenames = List.of(S3StoredFileName);

			RecordFileResponse recordFileResponse = RecordFileResponse.ofUpload(uploadFileNames,
					S3StoredFilenames);

			given(recordFileService.uploadRecordFiles(1L, 1L, uploadFiles, "user"))
					.willReturn(recordFileResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.post(
											"/api/v1/pets/{petId}/records/{recordId}/recordFiles/upload", 1L, 1L)
									.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andDo(print());

		}
	}

	@Nested
	@DisplayName("파일 삭제")
	class FileDelete {

		@Test
		@DisplayName("파일 삭제 성공")
		void delete_file_success() throws Exception {

			RecordFileResponse deleteRecordFileResponse = RecordFileResponse.ofDeleted(1L,
					"파일 삭제 완료");

			given(recordFileService.deleteRecordFile(1L, 1L, 1L, "user"))
					.willReturn(deleteRecordFileResponse);

			mockMvc.perform(
							RestDocumentationRequestBuilders.delete(
									"/api/v1/pets/{petId}/records/{recordId}/recordFiles/{recordFileId}",
									1L, 1L, 1L))
					.andExpect(status().isOk())
					.andDo(
							restDocs.document(
									pathParameters(
											parameterWithName("petId").description("반려동물 번호"),
											parameterWithName("recordId").description("일기 번호"),
											parameterWithName("recordFileId").description("일기 파일 번호")
									),
									responseFields(
											fieldWithPath("resultCode").description("결과코드"),
											fieldWithPath("result.message").description("결과메세지"),
											fieldWithPath("result.id").description("일기 파일 번호"),
											fieldWithPath("result.uploadFileName").description("업로드된 파일 이름"),
											fieldWithPath("result.s3StoredFileName").description("S3에 저장되는 파일 이름"))
							)
					);
		}
	}
}