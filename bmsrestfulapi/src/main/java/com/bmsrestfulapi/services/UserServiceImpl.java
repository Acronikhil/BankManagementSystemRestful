package com.bmsrestfulapi.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmsrestfulapi.entities.AccountInfo;
import com.bmsrestfulapi.entities.Login;
import com.bmsrestfulapi.entities.User;
import com.bmsrestfulapi.exceptions.CustomExceptionsMessages;
import com.bmsrestfulapi.exceptions.EmptyUserListException;
import com.bmsrestfulapi.exceptions.InvalidCredentialsException;
import com.bmsrestfulapi.exceptions.UserNotCreatedException;
import com.bmsrestfulapi.exceptions.UserNotFoundException;
import com.bmsrestfulapi.repositories.AccountInfoRepository;
import com.bmsrestfulapi.repositories.LoginRepository;
import com.bmsrestfulapi.repositories.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LoginRepository loginRepository;
	@Autowired
	private AccountInfoRepository accountInfoRepository;

	@Override
	public List<User> getAllNotVerifiedUser() throws EmptyUserListException {
		List<User> userList = userRepository.getNotVerifiedUsers();
		if (!userList.isEmpty()) {
			return userList;

		}
		throw new EmptyUserListException(CustomExceptionsMessages.EVERYONE_ALREADY_VERIFIED);

	}

	@Override
	public String createUser(User user) throws UserNotCreatedException {
		if (userRepository.existsById(user.getUserId())) {
			throw new UserNotCreatedException(CustomExceptionsMessages.USER_ALREADY_EXIST);
		} else if (userRepository.existByContactNo(user.getContactNo()) != null) {
			throw new UserNotCreatedException(CustomExceptionsMessages.USER_ALREADY_EXIST_WITH_SAME_CONTACT_NO);
		} else if (user.getName().equals(UserService.STRING) || user.getAddress().equals(UserService.STRING)
				|| user.getContactNo() == 0 || user.getGender().equals(UserService.STRING) || user.getPin() == 0) {
			throw new UserNotCreatedException(CustomExceptionsMessages.PLEASE_CHECK_DETAILS);

		} else {
			if (user.getGender().equalsIgnoreCase("male") || user.getGender().equalsIgnoreCase("female")) {
				user.setName(user.getName().toLowerCase());
				user.setGender(user.getGender().toLowerCase());
				User u = userRepository.save(user);
				u.getLogin().setAccountNo(u.getAccountList().get(0).getAccountNo());
				return "User created Successfully\nDetails:\n" + u;
			} else {
				throw new UserNotCreatedException(CustomExceptionsMessages.WRONG_GENDER_INPUT);
			}

		}
	}

	@Override
	public String verifyUser(Integer userId, Integer adminId)
			throws InvalidCredentialsException, UserNotFoundException {
		if (userRepository.existsById(adminId)) {
			User admin = userRepository.getById(adminId);
			if (admin.getRole().getRoleName().equalsIgnoreCase(UserService.ADMIN)) {
				if (userRepository.existsById(userId)) {
					Login login = loginRepository.getLoginById(userId);
					login.setVerified(true);
					return " User verified Successfully.";
				} else {
					throw new UserNotFoundException(CustomExceptionsMessages.NO_USER_EXISTS_WITH_THIS_ID);
				}
			} else {
				throw new InvalidCredentialsException(CustomExceptionsMessages.YOU_ARE_NOT_ADMIN_CONTACT_TO_BM);
			}
		} else {
			throw new UserNotFoundException(CustomExceptionsMessages.NO_ADMIN_EXIST_BY_ID);
		}
	}

	@Override
	public String checkBalance(Integer userId, Integer pin) throws InvalidCredentialsException {
		User user = userRepository.getPin(pin, userId);
		if (user != null && user.getPin().equals(pin) && user.getUserId().equals(userId)) {
			return "Your current balance is: " + accountInfoRepository.getBalance(userId);
		} else
			throw new InvalidCredentialsException(CustomExceptionsMessages.PLEASE_CHECK_YOUR_PIN_AND_USER_ID);
	}

	@Override
	public String withdrawMoney(Integer accountNo, Integer amount, Integer pin) throws InvalidCredentialsException {
		AccountInfo accountInfo = accountInfoRepository.verifyPin(pin, accountNo);
		if (accountInfo == null) {
			throw new InvalidCredentialsException(CustomExceptionsMessages.INVALID_CREDENTIAL);
		}
		User user = accountInfo.getUser();

		if (user != null && user.getPin().equals(pin) && accountInfo.getAccountNo().equals(accountNo)
				&& accountInfo.getUser().getUserId().equals(user.getUserId())) {
			Integer availableBalance = accountInfo.getCurrentBalance();
			if (amount <= availableBalance) {
				availableBalance -= amount;
				accountInfo.setCurrentBalance(availableBalance);
				accountInfoRepository.save(accountInfo);
				return "Money Withdrawn Successfully! \nYour available balance is: " + availableBalance;
			} else {
				return "Insufficient Balance";
			}
		} else {
			throw new InvalidCredentialsException(CustomExceptionsMessages.PLEASE_CHECK_YOUR_PIN_AND_ACCOUNT_NUMBER);
		}
	}

	@Override
	public String moneyTransfer(Integer accountNo, Integer receiversAccountNo, Integer amount, Integer pin)
			throws InvalidCredentialsException {
		AccountInfo accountInfo = accountInfoRepository.verifyPin(pin, accountNo);
		if (accountInfo == null) {
			throw new InvalidCredentialsException(CustomExceptionsMessages.INVALID_CREDENTIAL);
		}
		User user = accountInfo.getUser();
		AccountInfo receiverAccountInfo = accountInfoRepository.getAccountNo(receiversAccountNo);

		if (user != null && receiverAccountInfo != null && user.getPin().equals(pin)
				&& accountInfo.getAccountNo().equals(accountNo)
				&& receiverAccountInfo.getAccountNo().equals(receiversAccountNo)
				&& accountInfo.getUser().getUserId().equals(user.getUserId())) {
			Integer availableBalance = accountInfo.getCurrentBalance();
			Integer receiversAvailableBalance = receiverAccountInfo.getCurrentBalance();
			if (amount <= availableBalance) {
				availableBalance -= amount;
				accountInfo.setCurrentBalance(availableBalance);
				accountInfoRepository.save(accountInfo);

				receiversAvailableBalance += amount;
				receiverAccountInfo.setCurrentBalance(receiversAvailableBalance);
				accountInfoRepository.save(receiverAccountInfo);
				return "Money Transfered Successfully! \nYour current balance is: " + availableBalance;
			} else {
				return "Insufficient Balance";
			}
		} else {
			throw new InvalidCredentialsException(CustomExceptionsMessages.PLEASE_ENTER_VALID_CREDENTIALS);
		}
	}

	@Override
	public String deleteUserById(Integer userId, Integer adminId)
			throws UserNotFoundException, InvalidCredentialsException {
		User admin = userRepository.getById(adminId);
		if (admin.getRole().getRoleName().equalsIgnoreCase(UserService.ADMIN)) {
			if (userRepository.existsById(userId)) {
				userRepository.deleteById(userId);
				return "User deleted successfully!";
			} else {
				throw new UserNotFoundException(CustomExceptionsMessages.NO_USER_EXISTS_WITH_THIS_ID);
			}
		} else {
			throw new InvalidCredentialsException(CustomExceptionsMessages.YOU_ARE_NOT_ADMIN_EXCEPTION);
		}
	}

	@Override
	public String updateUser(User user, Integer adminId) throws UserNotFoundException, InvalidCredentialsException {
		if (userRepository.existsById(adminId)) {
			User admin = userRepository.getById(adminId);
			if (admin.getRole().getRoleName().equalsIgnoreCase(UserService.ADMIN)) {
				if (userRepository.existsById(user.getUserId())) {
					userRepository.save(user);
					return "User Updated successfully!";
				} else {
					throw new UserNotFoundException(CustomExceptionsMessages.NO_USER_EXISTS_WITH_THIS_ID);
				}
			} else {
				throw new InvalidCredentialsException(CustomExceptionsMessages.YOU_ARE_NOT_ADMIN_CANT_UPDATE_USER);
			}
		} else {
			throw new InvalidCredentialsException(CustomExceptionsMessages.NO_ADMIN_EXIST_BY_ID);
		}
	}

	@Override
	public String getAllUsers(Integer adminId)
			throws EmptyUserListException, InvalidCredentialsException, UserNotFoundException {
		if (userRepository.existsById(adminId)) {
			User admin = userRepository.getById(adminId);
			if (admin.getRole().getRoleName().equalsIgnoreCase(UserService.ADMIN)) {
				List<User> userList = userRepository.findAll();
				if (!userList.isEmpty()) {
					return "List of Users: \n" + userList;
				} else {
					throw new EmptyUserListException(CustomExceptionsMessages.NO_USER_EXIST_IN_DATABASE);
				}
			} else {
				throw new InvalidCredentialsException(CustomExceptionsMessages.YOU_ARE_NOT_ADMIN_CONTACT_TO_BM);
			}
		} else {
			throw new UserNotFoundException(CustomExceptionsMessages.NO_ADMIN_EXIST_BY_ID);
		}
	}
}
