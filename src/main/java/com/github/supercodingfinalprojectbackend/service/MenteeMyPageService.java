package com.github.supercodingfinalprojectbackend.service;


import com.github.supercodingfinalprojectbackend.dto.MenteeMyPageDto;
import com.github.supercodingfinalprojectbackend.entity.*;
import com.github.supercodingfinalprojectbackend.exception.errorcode.ApiErrorCode;
import com.github.supercodingfinalprojectbackend.repository.OrderSheetRepository;
import com.github.supercodingfinalprojectbackend.repository.PaymentRepository;
import com.github.supercodingfinalprojectbackend.repository.SelectedClassTimeRepository;
import com.github.supercodingfinalprojectbackend.repository.UserRepository;
import com.github.supercodingfinalprojectbackend.util.ResponseUtils;
import com.github.supercodingfinalprojectbackend.util.ValidateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MenteeMyPageService {
    private final UserRepository userRepository;
    private final OrderSheetRepository orderSheetRepository;
    private final SelectedClassTimeRepository selectedClassTimeRepository;
    private final PaymentRepository paymentRepository;
    public ResponseEntity<?> changeNickName(MenteeMyPageDto myPageDto) {

        User user = userRepository.findByUserIdAndIsDeletedIsFalse(myPageDto.getUserId()).orElseThrow(ApiErrorCode.NOT_FOUND_USER::exception);

        ValidateUtils.requireTrue(user.getNickname().matches(myPageDto.getNickname()), ApiErrorCode.MENTEE_MYPAGE_CHANGEINFO_BAD_REQUEST);

        MenteeMyPageDto.ResponseChangeInfo responseChangeInfo = MenteeMyPageDto.ResponseChangeInfo.builder().nickname(myPageDto.getNickname()).build();

        user.changeUserNameNickname(myPageDto.getNickname());

        return ResponseUtils.ok("닉네임 변경에 성공하였습니다.", responseChangeInfo);
    }

    public ResponseEntity<?> getOrderList(Long userId) {

        List<OrderSheet> orderSheet = orderSheetRepository.findAllByMenteeUserUserIdAndIsCompletedIsFalse(userId);

        List<MenteeMyPageDto.ResponseOrderDto> responseOrderDtoList = orderSheet.stream()
                .sorted(Comparator.comparing(OrderSheet::getCreatedAt).reversed())
                .map(MenteeMyPageDto::from)
                .collect(Collectors.toList());

        MenteeMyPageDto.ResponseOrderList responseOrderList = MenteeMyPageDto.ResponseOrderList.builder()
                .orderDtoList(responseOrderDtoList)
                .build();

        return ResponseUtils.ok("성공적으로 조회하였습니다", responseOrderList);
    }

        public ResponseEntity<?> getMenteeTransactionList(Long userId) {

            List<OrderSheet> orderSheet = orderSheetRepository.findAllByMenteeUserUserIdAndIsCompletedIsTrue(userId);


            List<MenteeMyPageDto.ResponseTransactionDto> lists = new ArrayList<>();


            for (OrderSheet orderSheets : orderSheet) {
                Payment payments = paymentRepository.findByOrderSheet_OrderSheetId(orderSheets.getOrderSheetId());
                List<SelectedClassTime> selectedClassTime = selectedClassTimeRepository.findAllByOrderSheet(orderSheets);
                List<String> convertTime =  convertToFormattedStrings(selectedClassTime);
                Posts posts = orderSheets.getPost();
                MenteeMyPageDto.ResponseTransactionDto responseTransactionDto = MenteeMyPageDto.from(convertTime,posts,orderSheets,payments);
                lists.add(responseTransactionDto);
            }
            lists.sort(Comparator.comparing(dto -> dto.getCreatedAt(),Comparator.reverseOrder()));
            return ResponseUtils.ok("성공적으로 조회하였습니다.", lists);
        }

    private List<String> convertToFormattedStrings(List<SelectedClassTime> classTimes) {
        List<String> formattedClassTimes = classTimes.stream()
                .map(classTime -> {
                    int year = classTime.getYear();
                    int month = classTime.getMonth();
                    int day = classTime.getDay();
                    int hour = classTime.getHour();
                    LocalDateTime dateTime = LocalDateTime.of(year,month,day,hour,0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
                    String formattedDateTime = dateTime.format(formatter);
                    return formattedDateTime;
                })
                .collect(Collectors.toList());

        return formattedClassTimes;
    }
}