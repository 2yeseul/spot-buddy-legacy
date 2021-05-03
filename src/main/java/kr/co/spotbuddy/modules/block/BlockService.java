package kr.co.spotbuddy.modules.block;

import kr.co.spotbuddy.infra.domain.Block;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.block.dto.BlockList;
import kr.co.spotbuddy.modules.block.dto.BlockRequest;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.response.IdResponse;
import kr.co.spotbuddy.modules.tour.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final TourRepository tourRepository;
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void cancelBlock(Member doBlock, BlockRequest blockRequest) {
        Member getBlocked = memberRepository.findByNickname(blockRequest.getNickname());
        if(blockRepository.isAlreadyBlocked(doBlock, getBlocked)) {
            blockRepository.deleteByDoBlockAndGetBlocked(doBlock, getBlocked);
        }
    }

    // 내 차단 리스트
    public List<BlockList> myBlockList(Member doBlock) {
        List<BlockList> blockLists = new ArrayList<>();
        if(blockRepository.existsByDoBlock(doBlock)) {
            List<Block> blocks = blockRepository.findAllByDoBlockOrderByIdDesc(doBlock);

            for(Block block : blocks) {
                BlockList blockList = BlockList.builder()
                        .nickname(block.getGetBlocked().getNickname())
                        .build();

                blockLists.add(blockList);
            }
        }

        return blockLists;
    }

    // 차단 - 동행 상세
    @Transactional
    public void blockTourUploader(Long id, Member doBlock) {
        Member getBlocked = tourRepository.findById(id).get().getMember();
        // 아직 블락하지 않은 경우에만 저장
        blockUpdate(doBlock, getBlocked);
    }

    @Transactional
    public void blockMember(Member doBlock, BlockRequest blockRequest) {
        Member getBlocked = memberRepository.findByNickname(blockRequest.getNickname());
        blockUpdate(doBlock, getBlocked);
    }

    private void blockUpdate(Member doBlock, Member getBlocked) {
        // 아직 블락하지 않은 경우에만 저장
        if (!blockRepository.isAlreadyBlocked(doBlock, getBlocked)) {
            Block block = Block.builder()
                    .doBlock(doBlock)
                    .getBlocked(getBlocked)
                    .build();

            blockRepository.save(block);
        }
    }

    public void blockChatSender() {

    }

    public void getMyBlockTourList(Member member) {
        List<Block> blockList = blockRepository.findAllByDoBlock(member);
        List<IdResponse> blockTourList = new ArrayList<>();
    }

}
