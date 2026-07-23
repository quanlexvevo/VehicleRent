package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //anotasyonumuzu yazdık
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired //udemydeki ve notlardaki bu ondan onda gitme durumunu sağlayan durum bu, constructor enjection deniyor.
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() { // bütün müşterileri getiriyor
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) { //belli bir idye sahip müşteriyi getiriyor
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existingCustomer = getCustomerById(id);

        existingCustomer.setFullName(updatedCustomer.getFullName());
        existingCustomer.setNationalId(updatedCustomer.getNationalId());
        existingCustomer.setPhone(updatedCustomer.getPhone());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setLicenseNumber(updatedCustomer.getLicenseNumber()); //setli olanlarda once javada yazılıyor

        return customerRepository.save(existingCustomer); // sql e burda geçiriyor güncelliyor
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}