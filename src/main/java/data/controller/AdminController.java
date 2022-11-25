package data.controller;

import data.dto.HostDto;
import data.dto.MemberDto;
import data.dto.NoticeDto;
import data.dto.RoomDto;
import data.mapper.HostMapper;
import data.mapper.MemberMapper;
import data.mapper.NoticeMapper;
import data.mapper.RoomMapper;
import data.util.ChangeName;
import data.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
public class AdminController {

    //파일 첨부를 위한 변수 선언
    String uploadFileName;
    ArrayList<String> uploadFileNames = new ArrayList<>();
    @Autowired
    MemberMapper memberMapper;

    @Autowired
    HostMapper hostMapper;

    @Autowired
    RoomMapper roomMapper;

    @Autowired
    NoticeMapper noticeMapper;
    
    //관리자 페이지에서 멤버정보 가져오기
    @GetMapping("/admin/memberList")
    public List<MemberDto> getMemberSearchList(
            @RequestParam String searchWord,
            @RequestParam String sort)
    {
        //saerchWord 넘어오는지 테스트
        System.out.println("Member searchWord = "+searchWord);
        System.out.println("Member sort = "+sort);

        HashMap<String, Object> map = new HashMap<>();
        map.put("searchWord",searchWord);
        map.put("sort",sort);

        //map 출력 테스트
        System.out.println("MemberList map = "+ map);

        return memberMapper.getMemberSearchList(map);
    }

    //관리자 페이지에서 멤버 활성상태 업데이트
    @GetMapping("/admin/memberActive")
    public void updateMemberAcitve(@RequestParam int userNum)
    {
        //num 값 확인
        System.out.println("num = "+userNum);

        memberMapper.updateMemberActive(userNum);
    }

    @GetMapping("/admin/memberPassReset")
    public void updateMemberPassword(@RequestParam int userNum)
    {
        //num 값 확인
        System.out.println("num = "+userNum);


        memberMapper.updateMemberPassword(userNum);
    }



    //관리자 페이지에서 호스트정보 가져오기
    @GetMapping("/admin/hostList")
    public List<HostDto> getHostSearchList(
            @RequestParam String searchWord,
            @RequestParam String sort)
    {
        //saerchWord 넘어오는지 테스트
        System.out.println("Host searchWord = "+searchWord);
        System.out.println("Host sort = "+sort);
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("searchWord",searchWord);
        map.put("sort",sort);

        //map 출력 테스트
        System.out.println("HostList map = "+ map);
        
        return hostMapper.getHostSearchList(map);
    }

    //admin > host warningCount reset
    @GetMapping("/admin/hostWCount")
    public void updateHostWarning(@RequestParam int hostNum)
    {
        //num 값 확인
        System.out.println("경고초기화 hostNum = "+hostNum);

        hostMapper.updateHostWarning(hostNum);
    }

    //admin > host warningCount reset
    @GetMapping("/admin/hostPass")
    public void updateHostPassword(@RequestParam int hostNum)
    {
        //num 값 확인
        System.out.println("비번초기화 hostNum = "+hostNum);

        hostMapper.updateHostPassword(hostNum);
    }


    //관리자페이지 > 공간관리 : 방 리스트 가져오기
    @GetMapping("/admin/spaceList")
    public List<RoomDto> getSpaceList(
            @RequestParam String searchWord,
            @RequestParam String sort)
    {
        System.out.println("Space sort = "+sort);
        System.out.println("Space searchWord = "+ searchWord);

        HashMap<String, Object> map = new HashMap<>();
        map.put("searchWord",searchWord);
        map.put("sort",sort);

        System.out.println("SpaceList map = "+ map);

        return roomMapper.getSpaceSearchList(map);
    }

    //관리자페이지 > 공간관리 : 공간 승인하기
    @GetMapping("/admin/approveSpace")
    public void approveSpace(@RequestParam int roomNum)
    {
        //넘어온 방 번호 확인
        System.out.println("대상 방번호 = "+roomNum);
        
        //방 번호 넘기기
        roomMapper.approveSpace(roomNum);
    }

    //관리자페이지 > 공간관리 : 공간 거부하기
    @GetMapping("/admin/rejectSpace")
    public void rejectSpace(@RequestParam int roomNum)
    {
        //넘어온 방 번호 확인
        System.out.println("대상 방번호 = "+roomNum);

        //방 번호 넘기기
        roomMapper.rejectSpace(roomNum);
    }


    //관리자 페이지에서 공지사항 리스트 가져오기
    @GetMapping("/admin/noticeList")
    public List<NoticeDto> getNoticeSearchList(
            @RequestParam String searchWord)
    {
        //saerchWord 넘어오는지 테스트
        System.out.println("Notice searchWord = "+searchWord);

        HashMap<String, Object> map = new HashMap<>();
        map.put("searchWord",searchWord);

        //map 출력 테스트
        System.out.println("NoticeList map = "+ map);

        return noticeMapper.getNoticeSearchList(map);
    }

    //관리자 페이지에서 공지사항 작성하기
    @PostMapping("/admin/noticeInsert")
    public void noticeInsert (@RequestBody MultipartFile uploadFile,
                              HttpServletRequest request,
                              @RequestParam String noticeType,
                              String noticeTitle,
                              String noticeContent
                              ){

        //DB에 Insert하기위해 map 선언
        HashMap<String, Object> map = new HashMap<>();

        //uploadFile을 제외하고 map에 담기
        map.put("noticeType",noticeType);
        map.put("noticeTitle",noticeTitle);
        map.put("noticeContent",noticeContent);

        //파일을 첨부했는지 안했는지 체크
        try {

            //upload 파일첨부를 했을때
//            if(!uploadFile.isEmpty()) {
            if(uploadFile != null) {

                // 업로드할 폴더의 경로(path) 구하기
                String path = request.getSession().getServletContext().getRealPath("/image");

                //기존 업로드 파일이 있을 경우 path 경로에서 파일 삭제 후 다시 업로드
                if (uploadFileName != null) {
                    FileUtil.deletePhoto(path, uploadFileName);   //있을 경우 path 경로의 uploadFileName 을 지운다
                }

                //업로드 파일을 변수에 담기
                uploadFileName = uploadFile.getOriginalFilename();

                //파일명을 날짜타입으로 변경
                uploadFileName = ChangeName.getChangeFileName(uploadFile.getOriginalFilename());

                //path 경로에 파일 업로드 진행
                uploadFile.transferTo(new File(path + "/" + uploadFileName));

                //성공 시 콘솔에 찍기
                System.out.println("파일 업로드 성공 -> 경로 // 파일명 " + path + "//" +uploadFileName );

                //map 에 uploadFile 담기
                map.put("uploadFile",uploadFileName);

            //upload 파일첨부를 안했을때
            }else {

                //map 에 uploadFile null 로 담기
                map.put("uploadFile",null);
            }

        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // insert sql 에 map 전달
        noticeMapper.noticeInsert(map);
    }

    //관리자 페이지에서 공지사항 삭제하기
    @DeleteMapping("/admin/deleteNotice")
    public void deleteNotice(
            @RequestParam int num)
    {
        //넘어온 방 번호 확인
        System.out.println("delete num값 확인 = "+num);

        //방 번호 넘기기
        noticeMapper.deleteNotice(num);
    }

    //관리자 페이지에서 공지사항 리스트 가져오기 (modal에 띄우기)
    @GetMapping("/admin/getNoticeInfo")
    public NoticeDto getNoticeInfo(
            @RequestParam int num)
    {
        //넘어온 Notice 번호 확인
        System.out.println("num값 확인 = " + num);

        //num 값 전달
        return noticeMapper.getNoticeInfo(num);
    }


    //관리자 페이지에서 공지사항 수정하기
    @PostMapping("/admin/updateNotice")
    public void updateNotice (@RequestBody MultipartFile uploadFile,
                              HttpServletRequest request,
                              @RequestParam String noticeType,
                              String noticeTitle,
                              String noticeContent,
                              int num
    ){

        //DB에 update하기위해 map 선언
        HashMap<String, Object> map = new HashMap<>();

        //uploadFile을 제외하고 map에 담기
        map.put("noticeType",noticeType);
        map.put("noticeTitle",noticeTitle);
        map.put("noticeContent",noticeContent);
        map.put("num",num);

        //파일을 첨부했는지 안했는지 체크
        try {

            //upload 파일첨부를 했을때
            if(uploadFile != null) {

                // 업로드할 폴더의 경로(path) 구하기
                String path = request.getSession().getServletContext().getRealPath("/image");

                //기존 업로드 파일이 있을 경우 path 경로에서 파일 삭제 후 다시 업로드
                if (uploadFileName != null) {
                    FileUtil.deletePhoto(path, uploadFileName);   //있을 경우 path 경로의 uploadFileName 을 지운다
                }

                //업로드 파일을 변수에 담기
                uploadFileName = uploadFile.getOriginalFilename();

                //파일명을 날짜타입으로 변경
                uploadFileName = ChangeName.getChangeFileName(uploadFile.getOriginalFilename());

                //path 경로에 파일 업로드 진행
                uploadFile.transferTo(new File(path + "/" + uploadFileName));

                //성공 시 콘솔에 찍기
                System.out.println("파일 업로드 성공 -> 경로 // 파일명 " + path + "//" +uploadFileName );

                //map 에 uploadFile 담기
                map.put("uploadFile",uploadFileName);

                //upload 파일첨부를 안했을때
            }else {

                //map 에 uploadFile null 로 담기
                map.put("uploadFile",null);
            }

        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // insert sql 에 map 전달
        noticeMapper.noticeInsert(map);
    }


}


