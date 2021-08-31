package Rasmus.DairyAPI;

import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


// tag::hateoas-imports[]
// end::hateoas-imports[]

@RestController
class ProductController {

    private final ProductRepository repository;

    ProductController(ProductRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/products")
    List<Product> all() {
        return repository.findAll();
    }
    // end::get-aggregate-root[]

    @PostMapping("/products")
    Product newProduct(@RequestBody Product newProduct) {
        return repository.save(newProduct);
    }

    // Single item

    @GetMapping("/products/{id}")
    EntityModel<Product> one(@PathVariable Long id) {

        Product product = repository.findById(id) //
                .orElseThrow(() -> new ProductNotFoundException(id));

        return EntityModel.of(product, //
                linkTo(methodOn(ProductController.class).one(id)).withSelfRel(),
                linkTo(methodOn(ProductController.class).all()).withRel("products"));
    }



    @PutMapping("/products/{id}")
    Product replaceProduct(@RequestBody Product newProduct, @PathVariable Long id) {

        return repository.findById(id)
                .map(product -> {
                    product.setName(newProduct.getName());
                    product.setRole(newProduct.getRole());
                    return repository.save(product);
                })
                .orElseGet(() -> {
                    newProduct.setId(id);
                    return repository.save(newProduct);
                });
    }

    @DeleteMapping("/product/{id}")
    void deleteProduct(@PathVariable Long id) {
        repository.deleteById(id);
    }
}