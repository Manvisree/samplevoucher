package com.voucherservice.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.voucherservice.entity.Voucher;
import com.voucherservice.exception.DataIsNotInsertedException;
import com.voucherservice.exception.NoVoucherPresentException;
import com.voucherservice.helper.Excelhelper;
import com.voucherservice.repository.VoucherRepository;

@Service
public class VoucherServiceImpl implements VoucherService {
	

	@Autowired
	 private VoucherRepository voucherRepo;
	
	@Override
	public List<Voucher> saveAllVouchers(MultipartFile file) throws IOException, DataIsNotInsertedException {
		
		List<Voucher> list = new ArrayList<>();
		
		List<Voucher> convertedList = Excelhelper.convertExcelToListOfVoucher(file.getInputStream());
		
		System.out.println(convertedList);
		
		list = voucherRepo.saveAll(convertedList);
		
		
		if(list.isEmpty())
		{
			throw new DataIsNotInsertedException();
		}
		
		return list;
	}

	@Override
	public List<Voucher> getAllVouchers() throws NoVoucherPresentException {
		
		List<Voucher> list = voucherRepo.findAll();
		
		if(list.isEmpty())
		{
			throw new NoVoucherPresentException();
		}
		
		List<Voucher> availableList = new ArrayList<>();
		LocalDate today = LocalDate.now();
		
		for(Voucher v : list)
		{
			if(v.getExpiryDate().isAfter(today))
			{
				if(v.getIssuedTo()==null)
				{
					availableList.add(v);
				}
				
			}
		}
		
		
		
		return availableList;
	}

	@Override
	public List<Voucher> getAllVoucherByExamName(String examName) throws NoVoucherPresentException {
		List<Voucher> allVouchersByName = new ArrayList<>();
		List<Voucher> allVouchersAvailable = new ArrayList<>();
		allVouchersByName = voucherRepo.findByExamName(examName);
		for (Voucher v : allVouchersByName) {
			LocalDate date =LocalDate.now();
			if (v.getExpiryDate().isAfter(date) || v.getIssuedTo()!=null) {
				allVouchersAvailable.add(v);
			}
		}
		if (allVouchersAvailable.isEmpty()) {
			throw new NoVoucherPresentException();
		}
		return allVouchersAvailable;
 
	}
 
	@Override
	public List<Voucher> getAllVoucherByCloudPlatform(String cloudPlatform) throws NoVoucherPresentException {
 
		List<Voucher> allVouchersByPlatform = new ArrayList<>();
		List<Voucher> allVouchersAvailable = new ArrayList<>();
		allVouchersByPlatform = voucherRepo.findByCloudPlatform(cloudPlatform);
		for (Voucher v : allVouchersByPlatform) {
			LocalDate date = LocalDate.now();
			if (v.getExpiryDate().isAfter(date) || v.getIssuedTo()!=null) {
				allVouchersAvailable.add(v);
			}
		}
		if (allVouchersAvailable.isEmpty()) {
			throw new NoVoucherPresentException();
 
		} else {
			return allVouchersAvailable;
		}
	}
 
	@Override
	public List<Voucher> getAllExpiredVoucher() throws NoVoucherPresentException {
		List<Voucher> list = voucherRepo.findAll();
		List<Voucher> expiredList = new ArrayList<>();
		for (Voucher v : list) {
			LocalDate date = LocalDate.now();
			if (v.getExpiryDate().isBefore(date) || v.getIssuedTo()!=null) {
				expiredList.add(v);
			}
		}
		if (expiredList.isEmpty()) {
			throw new NoVoucherPresentException();
 
		} else {
			return expiredList;
		}
	}
 
	@Override
	public Voucher getVoucherById(String id) throws NoVoucherPresentException {
		Optional<Voucher> opt = voucherRepo.findById(id);
//		Voucher voucher = opt.get();
		if (opt.isPresent()) {
			return opt.get();
		} else {
			throw new NoVoucherPresentException();
		}
 
	}

	@Override
	public Voucher assignUserEmail(String voucherId,String userEmail) throws NoVoucherPresentException {
		Optional<Voucher> opt = voucherRepo.findById(voucherId);
		if(opt.isPresent())
		{
			Voucher voucher = opt.get();
			voucher.setIssuedTo(userEmail);
			Voucher v =voucherRepo.save(voucher);
			return v;
		}else {
			throw new NoVoucherPresentException();
		}
		
		
	}

	@Override
	public String removeVoucherById(String id) throws NoVoucherPresentException {
		
		Optional<Voucher> voucher = voucherRepo.findById(id);
		
		if(voucher.isEmpty())
		{
			throw new NoVoucherPresentException();
		}
		
		voucherRepo.deleteById(id);
		
		return "Voucher Deleted Successfully";
	}

}
