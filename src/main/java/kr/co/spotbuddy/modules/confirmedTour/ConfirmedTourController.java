package kr.co.spotbuddy.modules.confirmedTour;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.confirmedTour.dto.CancelResponse;
import kr.co.spotbuddy.modules.confirmedTour.dto.CancelTour;
import kr.co.spotbuddy.modules.confirmedTour.dto.ChatRoomIdDto;
import kr.co.spotbuddy.modules.confirmedTour.dto.ConfirmedMember;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import kr.co.spotbuddy.modules.response.ConfirmTourResponse;
import kr.co.spotbuddy.modules.tour.TourRepository;
import kr.co.spotbuddy.modules.tour.dto.TourList;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConfirmedTourController {

    private final MemberType memberType;
    private final ConfirmedTourService confirmedTourService;
    private final TourRepository tourRepository;

    @GetMapping("/chat/all-confirm/{id}")
    public ConfirmTourResponse isAllConfirmChat(@PathVariable Long id) {
        return confirmedTourService.isAllConfirmedTour(id);
    }

    @GetMapping("/chat/is-confirmed/{id}")
    public ConfirmTourResponse isConfirmedChat(@PathVariable Long id, @CurrentUser Object object) {
        return confirmedTourService.isConfirmedChat(id, object);
    }

    // 해당 동행 글에서 사용자의 확정 여부
    @GetMapping("/is-confirmed/{id}")
    public ConfirmTourResponse isConfirmed(@CurrentUser Object object, @PathVariable Long id) {
        return confirmedTourService.isUserConfirmedTour(object, id);
    }

    // 동행 확정
    @PostMapping("/confirm-tour/{id}")
    public void confirmTour(@CurrentUser Object object, @PathVariable Long id, @RequestBody ChatRoomIdDto chatRoomIdDto) throws Exception {
        confirmedTourService.confirmNewTour(object, id, chatRoomIdDto);
    }

    // 내 동행 참여 목록
    @GetMapping("/my-confirm-tour")
    public List<TourList> getMyConfirmedTour(@CurrentUser Object object) {
        return confirmedTourService.getMyConfirmedTourList(object);
    }

    // 동행 모집에 참여한 회원 목록
    @GetMapping("/confirm-member/{id}")
    public ConfirmedMember getConfirmMember(@PathVariable Long id, @CurrentUser Object object) {
        Member member = memberType.getMemberType(object);

        return confirmedTourService.getConfirmedMemberList(id, member);
    }

    // 참여한 동행 취소
    @PostMapping("/cancel-tour/{id}")
    public void cancelConfirmedTour(@CurrentUser Object object, @PathVariable Long id) {
        confirmedTourService.cancelTour(object, id);
    }


    @GetMapping("/cancel-tour/my-list")
    public List<CancelTour> myCancelTourList(@CurrentUser Object object) {
        return confirmedTourService.myCancelTourList(object);
    }

    @GetMapping("/cancel-state/{id}")
    public CancelResponse isCanceledTour(@CurrentUser Object object, @PathVariable Long id) {
        return confirmedTourService.isCanceledTour(object, id);
    }
}
