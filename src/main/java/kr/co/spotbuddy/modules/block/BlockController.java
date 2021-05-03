package kr.co.spotbuddy.modules.block;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.block.dto.BlockList;
import kr.co.spotbuddy.modules.block.dto.BlockRequest;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/block")
public class BlockController {

    private final BlockService blockService;
    private final MemberType memberType;

    @PostMapping("/cancel")
    public void cancelBlock(@CurrentUser Object object, @RequestBody BlockRequest blockRequest) {
        Member member = memberType.getMemberType(object);
        blockService.cancelBlock(member, blockRequest);
    }

    @GetMapping("/my-list")
    public List<BlockList> getMyBlockList(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        return blockService.myBlockList(member);
    }

    @PostMapping("/tour/{id}")
    public void doBlockInTour(@PathVariable Long id, @CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        blockService.blockTourUploader(id, member);
    }

    @PostMapping("/member")
    public void doBlockMember(@CurrentUser Object object, @RequestBody BlockRequest blockRequest) {
        Member member = memberType.getMemberType(object);
        blockService.blockMember(member, blockRequest);
    }

}
