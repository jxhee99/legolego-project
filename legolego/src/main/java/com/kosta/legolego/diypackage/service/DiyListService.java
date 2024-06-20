package com.kosta.legolego.diypackage.service;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.repository.DiyListRepository;
import com.kosta.legolego.diypackage.repository.DiyRepository;
import com.kosta.legolego.partner.repository.PartnerRepository;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class DiyListService {

    @Autowired
    private DiyListRepository  diyListRepository;

    @Autowired
    private DiyRepository diyRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ProductRepository productRepository;


    // 관리자가 모든 diylist를 볼 수 있도록 구현한 로직
    public List<DiyList> getAllDiyListsForAdmin() {
        return diyListRepository.findAll();
    }

    // 여행사가 제안한 diylist만 보이게 구현한 로직
    public List<DiyList> getDiyListsForPartner(Long partnerNum) {
        return diyListRepository.findAllByPartner_partnerNum(partnerNum);
    }

    // 사용자가 제작한 diylist만 보이게 구현한 로직
    public List<DiyList> getDiyListsForUser(Long userNum) {
        return diyListRepository.findAllByDiyPackage_User_userNum(userNum);
    }

    // 작성자 제안 수락
    public DiyList acceptProposal(Long listNum, Long packageNum){
        Optional<DiyList> diyListOptional = diyListRepository.findById(listNum);
        if(diyListOptional.isPresent()) {
            DiyList diyList = diyListOptional.get();

            diyList.setIsSelected(true);
            diyListRepository.save(diyList);

            // 같은 패키지에 대한 다른 제안받기 비활성화
            List<DiyList> otherProposals = diyListRepository.findAllByDiyPackage_packageNumAndIsSelected_Null(packageNum);
            for (DiyList proposal : otherProposals) {
                if(!proposal.getListNum().equals(listNum)) {
                    proposal.setIsSelected(false);
                    diyListRepository.save(proposal);
                }
            }
            return diyList;
        }
        throw new IllegalArgumentException("diy 리스트 번호가 유효하지 않습니다.");
    }

    // 상품 등록 승인 (only admin)
    public DiyList registerProduct(Long adminNum, Long listNum, String recruitmentDeadline) {

        // 관리자인지 확인
        if(!adminRepository.existsById(adminNum)) {
            throw new IllegalArgumentException("상품을 등록할 수 있는 권한이 없습니다.");
        }

        Optional<DiyList> diyListOptional = diyListRepository.findById(listNum);
        if(diyListOptional.isPresent()) {
            DiyList diyList = diyListOptional.get();

            // listNum이 true 값인 패키지만 상품 등록
            if(!Boolean.TRUE.equals(diyList.getIsSelected())){
                throw new IllegalArgumentException("diy 리스트 번호가 유효하지 않습니다.");
            }

            Admin admin = adminRepository.findByAdminNum(adminNum);
            if (admin == null) {
                throw new IllegalArgumentException("관리자를 찾을 수 없습니다.");
            }

            // String 타입을 Timestamp 타입으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime localDateTime = LocalDateTime.parse(recruitmentDeadline, formatter);
            Timestamp timestamp = Timestamp.valueOf(localDateTime);

            Product product = new Product();

            product.setDiyList(diyList);
            product.setRecruitmentDeadline(timestamp); // 입력받은 recruitmentDeadline를 product recruitmentDeadline에 삽입
            product.setRegDate(new Timestamp(System.currentTimeMillis()));
            product.setPrice(diyList.getPrice());
            product.setProductName(diyList.getDiyPackage().getPackageName());
            product.setProductImage(diyList.getDiyPackage().getProfileImg());
            product.setAdmin(admin);

            productRepository.save(product);

            // productNum을 diy_list에 저장
            diyList.setProductNum(product.getProductNum());
            diyList.setIsRegistered(true);
            diyListRepository.save(diyList);

            return diyList;
        }
        throw new IllegalArgumentException("diy 리스트 번호가 유효하지 않습니다.");
    }

}
